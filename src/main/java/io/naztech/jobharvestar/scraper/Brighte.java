package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Brighte Jobsite parser 
 * URL: https://brighte.com.au/careers/
 * 
 * @author sohid.ullah
 * @since 25.03.19
 ***/
@Service
@Slf4j
public class Brighte extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BRIGHTE;
	private WebClient webClient;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		List<Job> jobList = new ArrayList<>();
		HtmlPage page = webClient.getPage(site.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		List<HtmlElement> jobElement = page.getByXPath("//div[@class = 'cell m-bottom-30']");
		jobList.addAll(browseJobList(site, jobElement));
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
			saveJob(getJobDetails(job), site);
			}catch(Exception e) {
				exception = e;
			}
		}
	}

	private List<Job> browseJobList(SiteMetaData site, List<HtmlElement> jobElement) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		int total = jobElement.size();
		for (int i = 0; i < total; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			HtmlElement jobE = jobElement.get(i).getElementsByTagName("a").get(0);
			Job job = new Job(jobE.getAttribute("href"));
			job.setTitle(jobE.asText().trim());
			job.setName(job.getTitle());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) {
		try {
			HtmlPage page = webClient.getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(TIME_10S);
			HtmlElement jobE = page.getFirstByXPath("//div[@class = 'rake-job-location']");
			if (jobE != null)
				job.setLocation(jobE.getTextContent());
			jobE = page.getFirstByXPath("//div[@id = 'content']");
			if (jobE != null)
				job.setSpec(jobE.getTextContent());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed Parse job details " + job.getUrl(), e);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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