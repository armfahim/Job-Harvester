package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

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
 * Origami Job Site Parser<br>
 * URL: https://open.talentio.com/1/c/origami/requisitions/207
 * 
 * @author Arifur Rahman
 * @since 2019-03-31
 */
@Service
@Slf4j
public class Origami extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ORIGAMI;
	private WebClient client = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getChromeClient();
		startSiteScrapping(getSiteMetaData(SITE));

	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			HtmlPage page = client.getPage(siteMeta.getUrl());
			getSummaryPages(page, siteMeta);
		} catch (FailingHttpStatusCodeException e) {
			log.warn("Failed to load page" + siteMeta.getUrl() + e);
			throw e;
		}
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData site) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException, PageScrapingInterruptedException {
		List<HtmlElement> jobList = page.getBody().getByXPath("//div[@class='category-container']/ul/li/a");
		String base = "https:";
		expectedJobCount = jobList.size();
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setTitle(jobList.get(i).getTextContent());
			job.setUrl(base + jobList.get(i).getAttribute("href"));
			try {
				job = getJobDetails(job);
				saveJob(job, site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			HtmlElement jobSpec = page.getFirstByXPath("//div[@class='detail-container']");
			job.setSpec(jobSpec.asText());
			return job;

		} catch (HttpStatusCodeException e) {
			log.warn("Failed to load page" + job.getUrl() + e);
			return null;
		}

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
