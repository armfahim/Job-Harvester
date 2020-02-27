package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
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
 * British Petroleum.<br>
 * URL: https://jobs.bp.com/search-jobs?glat=23.753&glon=90.3748
 * 
 * @author naym.hossain
 * @author tanmoy.tushar
 * @since 2019-03-06
 */
@Slf4j
@Service
public class BritishPetroleum extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BRITISH_PETROLEUM;
	private String baseUrl;
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 19);
		List<String> jobList = new ArrayList<>();
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(5000);
		int totalPage = getTotalPage(page);
		for(int i = 0; i < totalPage; i++) {
			jobList.addAll(getSummaryPages(page));
			if (!hasNextPage(page))	break;
			List<HtmlElement> nextPage = page.getByXPath("//*[@id=\"pagination-bottom\"]/div[2]/a[2]");
			page = nextPage.get(0).click();
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_4S));
			client.waitForBackgroundJavaScript(5000);
		}
		for (String url : jobList) {
			if(isStopped()) throw new PageScrapingInterruptedException();			
			Job job = new Job(url);
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch(Exception e) {
				exception = e;
			}
		}
	}

	private List<String> getSummaryPages(HtmlPage page) throws PageScrapingInterruptedException {
		List<String> jobSummaryList = new ArrayList<>();
		List<HtmlElement> jobLinks = page.getByXPath("//*[@id=\"search-results-list\"]/ul/li/a");
		for (HtmlElement link : jobLinks) {
			if(isStopped()) throw new PageScrapingInterruptedException();			
			jobSummaryList.add(getBaseUrl() + link.getAttribute("href"));
		}
		return jobSummaryList;
	}

	private Job getJobDetails(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h1");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("span[class=job-info job-location]");
			if(jobE != null) job.setLocation(jobE.text());
			jobE = doc.selectFirst("span[class=job-info job-id]");
			if(jobE != null) job.setReferenceId(jobE.text().split("ID")[1].trim());
			jobE = doc.selectFirst("span[class=job-info job-cat]");
			if(jobE != null) job.setCategory(jobE.text().split("category")[1].trim());
			jobE = doc.selectFirst("a[class=button job-apply top]");
			if(jobE != null) job.setApplicationUrl(jobE.attr("href"));
			jobE = doc.selectFirst("div[class=ats-description]");
			job.setSpec(jobE.text());
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
			return null;
		}
	}

	private boolean hasNextPage(HtmlPage page) {
		List<HtmlElement> nextPage = page.getByXPath("//*[@id=\"pagination-bottom\"]/div[2]/a[2]");
		return !nextPage.get(0).getAttribute("class").equals("next disabled");
	}
	
	private int getTotalPage(HtmlPage page) {
		HtmlElement el = page.getFirstByXPath("//h1[@role='heading']");
		String totalJob = el.asText().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
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
