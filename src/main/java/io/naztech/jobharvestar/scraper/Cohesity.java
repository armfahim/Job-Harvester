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
 * Cohesity jobs site parse <br>
 * URL: https://www.cohesity.com/company/careers/open-positions/
 * 
 * @author tanmoy.tushar
 * @since 2019-03-11
 */
@Service
@Slf4j
public class Cohesity extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.COHESITY;
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
		page = client.getPage(page.getElementById("grnhse_iframe").getAttribute("src"));
		getSummaryPages(site, page);
	}

	private void getSummaryPages(SiteMetaData site, HtmlPage page) throws PageScrapingInterruptedException {
		try {
			List<HtmlElement> rowList = page.getBody().getByXPath("//div[@class='opening']/a");
			expectedJobCount = rowList.size();
			for (HtmlElement row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(row.getAttribute("href"));
				job.setTitle(row.getTextContent());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetails(job), site);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn("Failed to parse job list", e);
			throw e;
		}
	}

	private Job getJobDetails(Job job) {
		if (job.getUrl() == null)
			return null;
		try {
			HtmlPage page = client.getPage(job.getUrl());
			page = client.getPage(page.getElementById("grnhse_iframe").getAttribute("src"));
			HtmlElement jobE = page.getFirstByXPath("//div[@class='location']");
			job.setLocation(jobE.getTextContent().trim());
			jobE = page.getFirstByXPath("//div[@id='content']");
			job.setSpec(jobE.getTextContent().trim());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
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
