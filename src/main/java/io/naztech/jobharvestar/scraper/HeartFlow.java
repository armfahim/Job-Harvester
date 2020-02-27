package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
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
 * HeartFlow Job site Parser <br>
 * URL: https://www.heartflow.com/careers
 * 
 * @author Rahat Ahmad
 * @since 2019-03-12 https://www.heartflow.com/careers
 */
@Slf4j
@Service
public class HeartFlow extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.HEARTFLOW;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		client.waitForBackgroundJavaScript(20 * 1000);
		client.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
		client.getOptions().setTimeout(30 * 1000);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(TIME_5S);
		List<Job> jobList = getSummaryPage(page);
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job, page), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private List<Job> getSummaryPage(HtmlPage page) {

		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='cell-span-12']/div/div");
		List<Job> jobList = new ArrayList<>();
		try {
			for (HtmlElement htmlElement : jobLinksE) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
				job.setTitle(htmlElement.getElementsByTagName("a").get(0).getTextContent());
				job.setName(job.getTitle());
				job.setLocation(htmlElement.getElementsByTagName("div").get(1).getTextContent());
				jobList.add(job);
			}
		} catch (PageScrapingInterruptedException e) {
			log.warn("Failed to parse Site: " + getSiteName(), e);
		}
		return jobList;
	}

	private Job getJobDetails(Job job, HtmlPage page) {
		try {
			page = client.getPage(job.getUrl());
			client.waitForBackgroundJavaScript(TIME_5S);
			HtmlElement spec = page.getFirstByXPath("//div[@class='jv-job-detail-description']");
			job.setSpec(spec.getTextContent().trim());
		} catch (ElementNotFoundException | FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
		}
		return job;
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
