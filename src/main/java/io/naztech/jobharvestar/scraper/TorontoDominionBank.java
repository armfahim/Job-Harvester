package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
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
 * Toronto Dominion Bank<br>
 * URL: https://jobs.td.com/en/job-search-results
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-01-31
 */
@Service
@Slf4j
public class TorontoDominionBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TORONTO_DOMINION_BANK;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private String baseUrl;
	private static final int JOBPERPAGE = 10;
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		client = getChromeClient();
		client.getOptions().setTimeout(50000);
		startSiteScrapping(getSiteMetaData(ShortName.TORONTO_DOMINION_BANK));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 19);
		int totalPage = getTotalPages(siteMeta.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				getSummaryPages(siteMeta.getUrl() + "?pg=" + i, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page no " + i, e);
				continue;
			}
		}
	}

	private int getTotalPages(String url) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = client.getPage(url);
		client.waitForBackgroundJavaScript(5000);
		List<HtmlElement> list = page.getByXPath("//span[@id='live-results-counter']");
		expectedJobCount = Integer.parseInt(list.get(0).asText());
		return getPageCount(list.get(0).asText(), JOBPERPAGE);
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException, FailingHttpStatusCodeException, IOException {
		HtmlPage page1 = client.getPage(url);
		client.waitForBackgroundJavaScript(TIME_5S);
		List<HtmlElement> joblist = page1.getByXPath("//div[@class='jobTitle']/a");
		for (HtmlElement row : joblist) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + row.getAttribute("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job details", job.getUrl(), e);
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[id=gtm-jobdetail-id]");
		if (jobE != null) job.setReferenceId(jobE.text());
		jobE = doc.selectFirst("span[id=gtm-jobdetail-category]");
		if (jobE != null) job.setCategory(jobE.text());
		jobE = doc.selectFirst("span[id=gtm-jobdetail-city]");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("span[id=gtm-jobdetail-state]");
		if (jobE != null) job.setLocation(job.getLocation() + ", " + jobE.text());
		jobE = doc.selectFirst("span[id=gtm-jobdetail-date]");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text(), DF));
		jobE = doc.selectFirst("span[id=gtm-jobdetail-apply]>a");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("div[class=jdContent divJobDescription]");
		job.setSpec(jobE.text());
		jobE = doc.selectFirst("div[class=jdContent divRequirements]");
		if (jobE != null) job.setPrerequisite(jobE.text());
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
