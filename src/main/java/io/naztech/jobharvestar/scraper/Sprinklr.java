package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Sprinklr Recruiter jobs site parse <br>
 * URL: https://www.sprinklr.com/careers/?p=jobs
 * 
 * @author tanmoy.tushar
 * @since 2019-03-14
 */
@Service
@Slf4j
public class Sprinklr extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SPRINKLR;
	private static WebClient client;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 36);
		HtmlPage page = client.getPage(site.getUrl());
		client.setJavaScriptTimeout(TIME_10S*2);
		DomElement frameE = page.getElementById("jv_careersite_iframe_id");
		page = client.getPage("https:" + frameE.getAttribute("src"));
		client.setJavaScriptTimeout(TIME_10S*2);
		List<Job> jobList = new ArrayList<>();
		jobList.addAll(getSummaryPages(site, page));
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private List<Job> getSummaryPages(SiteMetaData site, HtmlPage page) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		try {
			List<HtmlElement> rowList = page.getBody().getByXPath("//td[@class='jv-job-list-name']/a");
			for (HtmlElement row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + row.getAttribute("href").substring(9));
				job.setTitle(row.asText());
				job.setName(job.getTitle());
				jobList.add(job);
			}
		} catch (FailingHttpStatusCodeException e) {
			log.info(getSiteName() + " Exception Occured", e);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			client.setJavaScriptTimeout(TIME_10S*2);
			DomElement frameE = page.getElementById("jv_careersite_iframe_id");
			page = client.getPage("https:" + frameE.getAttribute("src"));
			client.setJavaScriptTimeout(TIME_10S*2);
			HtmlElement jobE = page.getFirstByXPath("//p[@class='jv-job-detail-meta']");
			job.setLocation(jobE.asText());
			jobE = page.getFirstByXPath("//div[@class='jv-job-detail-description']");
			job.setSpec(jobE.asText());
			job.setApplicationUrl(job.getUrl() + "/apply");
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info(getSiteName() + " Failed parse job details" + job.getUrl(), e);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
