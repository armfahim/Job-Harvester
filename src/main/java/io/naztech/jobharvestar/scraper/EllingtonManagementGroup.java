package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Ellington Management Group Jobsite Parser<br>
 * URL: https://www.ellington.com/careers/job-search/
 * 
 * @author Rahat Ahmad
 * @since 2019-03-07
 */
@Service
@Slf4j
public class EllingtonManagementGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ELLINGTON_MANAGEMENT_GROUP;
	private static WebClient client = null;
	private static final String TOTAL_JOB = "//div[@class='oracletaleocwsv2-accordion oracletaleocwsv2-accordion-expandable clearfix']/div";
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(TIME_4S * 2);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		HtmlElement button = page.getFirstByXPath("//button[@class = 'btn btn-primary oracletaleocwsv2-btn-fa fa-search']");
		page = button.click();
		Thread.sleep(TIME_5S);
		List<Job> jobLinks = getSummaryPage(page);
		expectedJobCount = jobLinks.size();
		for (Job job : jobLinks) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job, page), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private List<Job> getSummaryPage(HtmlPage page) throws PageScrapingInterruptedException {
		List<HtmlElement> jobLinksE = page.getByXPath(TOTAL_JOB);
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement el : jobLinksE) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setUrl(el.getElementsByTagName("a").get(0).getAttribute("href"));
			job.setTitle(el.getElementsByTagName("a").get(0).asText());
			job.setName(job.getTitle());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job, HtmlPage page) {
		try {
			page = client.getPage(job.getUrl());
			client.waitForBackgroundJavaScript(TIME_4S * 2);
			HtmlElement spec = (HtmlElement) page.getByXPath("//div[@class='col-xs-12 col-sm-12 col-md-8']").get(0);
			job.setSpec(spec.asText());
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
