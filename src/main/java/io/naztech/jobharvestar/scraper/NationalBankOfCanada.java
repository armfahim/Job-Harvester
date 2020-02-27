package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
 * National Bank of Canada job site parsing class. <br>
 * URL: https://jobs.nbc.ca/search-jobs
 * 
 * @author assaduzzaman.sohan
 * @author tanmoy.tushar
 * @since 2019-03-04
 */
@Slf4j
@Service
public class NationalBankOfCanada extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NATIONAL_BANK_OF_CANADA;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 19);
		CLIENT = getFirefoxClient();
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_10S);
		int totalPage = getTotalPage(page);
		for (int i = 0; i < totalPage; i++) {
			getSummaryPages(page, siteMeta);
			if (i == totalPage - 1)
				break;
			HtmlElement next = page.getFirstByXPath("//a[@class='next']");
			next.click();
			Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_5S));
		}
	}

	private int getTotalPage(HtmlPage page) {
		HtmlElement el = page.getFirstByXPath("//p[@role='status']");
		String totalJob = el.asText().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 15);
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<HtmlElement> list = page.getBody().getByXPath("//section[@id='search-results-list']/ul/li");
		for (int i = 0; i < list.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + list.get(i).getElementsByTagName("a").get(0).getAttribute("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed parse job details " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[class=ats-description]>h3>span");
		if (jobE != null)
			job.setLocation(jobE.text());
		jobE = doc.select("div[class=ats-description]>div>h3>span").get(1);
		if (jobE != null)
			job.setType(jobE.text());
		jobE = doc.selectFirst("div[class=ats-description]");
		job.setSpec(jobE.text());
		jobE = doc.selectFirst("a[class=button job-apply bottom]");
		if (jobE != null)
			job.setApplicationUrl(jobE.attr("href"));
		Elements jobInfo = doc.select("div[class=ats-description]>div>h4");
		if (jobInfo.size() == 4) {
			if (jobInfo.get(0).text().contains("Field"))
				job.setCategory(jobInfo.get(0).text().split(":")[1].trim());
			if (jobInfo.get(2).text().contains("Number"))
				job.setReferenceId(jobInfo.get(2).text().split(":")[1].trim());
			if (jobInfo.get(3).text().contains("Posting"))
				job.setPostedDate(parseDate(jobInfo.get(3).text().split(":")[1].trim(), DF));
			if (jobInfo.get(1).text().contains("Unposting"))
				job.setDeadline(parseDate(jobInfo.get(1).text().split(":")[1].trim(), DF));
		}
		if (jobInfo.size() == 5) {
			if (jobInfo.get(1).text().contains("Field"))
				job.setCategory(jobInfo.get(1).text().split(":")[1].trim());
			if (jobInfo.get(3).text().contains("Number"))
				job.setReferenceId(jobInfo.get(3).text().split(":")[1].trim());
			if (jobInfo.get(4).text().contains("Posting"))
				job.setPostedDate(parseDate(jobInfo.get(4).text().split(":")[1].trim(), DF));
			if (jobInfo.get(2).text().contains("Unposting"))
				job.setDeadline(parseDate(jobInfo.get(2).text().split(":")[1].trim(), DF));
		}
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
		CLIENT.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
