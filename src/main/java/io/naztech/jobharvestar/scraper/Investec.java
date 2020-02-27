package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

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
 * Investec job site Parser<br>
 * URL: https://careers.investec.co.uk/jobs/vacancy/find/results
 * 
 * @author armaan.choudhury
 * @author tanmoy.tushar
 * @since 2019-02-12
 */
@Slf4j
@Service
public class Investec extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INVESTEC_PLC_GB;
	private static WebClient client;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.INVESTEC_PLC_GB));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 30);
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(TIME_10S);
		int totalPage = getTotalPage(page);
		expectedJobCount = totalPage * 9;
		getSummaryPage(page, siteMeta);
		for (int i = 1; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<HtmlElement> list = page.getByXPath("//div[@class = 'pagingButtons']");
			HtmlPage nextPage = list.get(0).getElementsByTagName("a").get(1).click();
			client.waitForBackgroundJavaScript(TIME_5S);
			getSummaryPage(nextPage, siteMeta);
			page = nextPage;
		}
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		List<HtmlElement> row = page.getByXPath("//div[@class = 'rowLabel']/a");
		for (HtmlElement el : row) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.getAttribute("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job deatil of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).timeout(TIME_10S).get();
		Element jobE = doc.selectFirst("div[class=JobTitle]>h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("li[class=jobSumItem SumItem_codelist5value]>div[class=jobSumValue]");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("li[class=jobSumItem SumItem_codelist4value]>div[class=jobSumValue]");
		if (jobE != null) job.setCategory(jobE.text());
		jobE = doc.selectFirst("li[class=jobSumItem SumItem_codelist11value]>div[class=jobSumValue]");
		if (jobE != null) job.setType(jobE.text());
		jobE = doc.selectFirst("a[class=buttonAnchor right]");
		if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		jobE = doc.selectFirst("div[class=earcu_posdescriptionnote]");
		job.setSpec(jobE.text());
		return job;
	}

	private int getTotalPage(HtmlPage page) {
		HtmlElement sec = page.getBody().getOneHtmlElementByAttribute("div", "class", "pagingText");
		return Integer.parseInt(sec.getTextContent().split("of")[1].trim());
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