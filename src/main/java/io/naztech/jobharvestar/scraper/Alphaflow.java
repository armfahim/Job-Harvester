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
 * Alphaflow jobs site parse <br>
 * URL: https://www.alphaflow.com/careers
 * 
 * @author sohid.ullah
 * @since 2019-03-27
 */
@Service
@Slf4j
public class Alphaflow extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ALPHAFLOW;
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
		HtmlPage htmlPage = webClient.getPage(site.getUrl());
		DomElement iframeE = htmlPage.getElementById("hire_widget_iframe_0");
		htmlPage = webClient.getPage(iframeE.getAttribute("src"));
		HtmlElement htmlBodyElement = htmlPage.getBody();
		List<HtmlElement> htmlElementList = htmlBodyElement.getByXPath("//li[@class='bb-public-jobs-list__job-item ptor-jobs-list__item']");
		jobList.addAll(browseJobList(site, htmlElementList));
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

	private List<Job> browseJobList(SiteMetaData site, List<HtmlElement> htmlElementList)
			throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		int numberOfJob = htmlElementList.size();
		for (int i = 0; i < numberOfJob; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			String url = htmlElementList.get(i).getElementsByTagName("a").get(0).getAttribute("href");
			Job job = new Job(url);
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) {
		try {
			HtmlPage jobDetailsPage = webClient.getPage(job.getUrl());
			HtmlElement jobDetailsElement = jobDetailsPage.getBody();
			HtmlElement headerElement = jobDetailsElement.getOneHtmlElementByAttribute("div", "class",
					"bb-jobs-posting__header");
			job.setTitle(headerElement.getElementsByTagName("h1").get(0).asText());
			job.setName(job.getTitle());
			job.setCategory(headerElement.getElementsByTagName("li").get(0).asText());
			job.setLocation(headerElement.getElementsByTagName("li").get(1).asText());
			String jobSpecificationElement = jobDetailsElement.getOneHtmlElementByAttribute("div", "class",
					"bb-rich-text-editor__content ptor-job-view-description public-job-description").asText();
			job.setSpec(jobSpecificationElement);
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
