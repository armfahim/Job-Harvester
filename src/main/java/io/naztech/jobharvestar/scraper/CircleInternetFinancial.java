package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * Circle Internet Financial jobs site parse <br>
 * URL: https://circle.careers/bd/#nwh-openings
 * 
 * @author tanmoy.tushar
 * @since 2019-03-12
 */
@Service
@Slf4j
public class CircleInternetFinancial extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CIRCLE_INTERNET_FINANCIAL;
	private static WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(site.getUrl());
		client.waitForBackgroundJavaScript(TIME_10S);
		List<HtmlElement> jobList = page.getBody().getByXPath("//div[@id='nwh-positions']/a");
		expectedJobCount = jobList.size();
		for (HtmlElement link : jobList) {
			Job job = new Job(link.getAttribute("href"));
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
			saveJob(getJobDetails(job), site);
			}catch(Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			HtmlElement jobE = page.getFirstByXPath("//h1[@class='app-title']");
			job.setTitle(jobE.asText());
			job.setName(job.getTitle());
			jobE = page.getFirstByXPath("//div[@class='location']");
			job.setLocation(jobE.asText());
			job.setApplicationUrl(job.getUrl() + "#app");
			jobE = page.getFirstByXPath("//div[@id='content']");
			job.setSpec(jobE.asText());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info(getSiteName() + " Failed parse job details of " + job.getUrl(), e);
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
