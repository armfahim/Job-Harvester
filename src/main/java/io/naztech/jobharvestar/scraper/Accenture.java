package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Accenture<br>
 * URL: https://www.accenture.com/in-en/careers/jobsearch
 * 
 * @author iftekar.alam
 * @since 2019-10-16
 */
@Slf4j
@Service
public class Accenture extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ACCENTURE;
	private String baseUrl;
	private static WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(ShortName.ACCENTURE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getFirefoxClient();
		webClient.getCookieManager().clearCookies();
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S * 3);
		int totalPageNo = getTotalPage(page);
		for (int i = 1; i < totalPageNo; i++) {
			List<HtmlElement> jobList = page.getByXPath("//div[@class='module job-card-wrapper col-md-4 col-xs-12 col-sm-6 corporate-regular background-white']");
			List<HtmlElement> dateList = page.getByXPath("//p[@class='posted-date small']");
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				browseJobList(jobList, dateList, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse list of ", siteMeta.getUrl());
			}
			if (i == totalPageNo - 1) break;
			HtmlElement click = page.getFirstByXPath("//div[@class='reinvent-pagination-next-container']/a");
			page = click.click();
			Thread.sleep(RandomUtils.nextInt(TIME_10S, TIME_10S + TIME_5S));
		}
	}

	private int getTotalPage(HtmlPage page) throws InterruptedException {
		HtmlElement totalJob = page.getFirstByXPath("//span[@class='search-results-count total-jobs']");
		String totalJ = totalJob.asText().replace("(", " ").replace(")", " ").trim();
		expectedJobCount = Integer.parseInt(totalJ);
		return getPageCount(totalJ, 9);
	}

	private void browseJobList(List<HtmlElement> jobList, List<HtmlElement> dateList, SiteMetaData siteMeta) throws InterruptedException {
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(jobList.get(i).getElementsByTagName("a").get(0).getAttribute("href"));
			String pDate = dateList.get(i).asText().split("Posted")[1].trim();
			if (pDate.contains("more than")) job.setPostedDate(parseAgoDates(pDate.split("than")[1].trim()));
			else job.setPostedDate(parseAgoDates(pDate));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.selectFirst("div[class=col-xs-12 module description]").text().trim());
		Element jobE = doc.getElementById("job-listing-hero");
		job.setTitle(jobE.attr("data-analytics-job-title"));
		job.setName(job.getTitle());
		job.setLocation(jobE.attr("data-analytics-job-location"));
		job.setReferenceId(jobE.attr("data-analytics-job-jobid"));
		jobE = doc.selectFirst("a[class=apply-job-btn reinvent-job-apply]");
		job.setApplicationUrl(jobE.attr("href"));
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
