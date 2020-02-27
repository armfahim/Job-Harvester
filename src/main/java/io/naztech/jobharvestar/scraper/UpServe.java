package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
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
 * UpServe Job Site Parser<br>
 * URL: https://upserve.com/company/careers/
 * 
 * @author Arifur Rahman
 * @author bm.alamin
 * @since 2019-03-31
 */
@Service
@Slf4j
public class UpServe extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UPSERVE;
	private WebClient client = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private final String JOB_SPEC_PATH = "//div[@class='col-md-12']";
	private final String JOB_ITEMS_PATH = "//ul[@class='tr-list job-meta list-inline']/";

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
			log.warn("Failed to load page" + siteMeta.getUrl());
			throw e;
		}
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData site)
			throws MalformedURLException, IOException, PageScrapingInterruptedException {
		List<HtmlElement> jobTitleList = page.getBody().getByXPath("//a[@class='job-title']");
		expectedJobCount = jobTitleList.size();
		for (int i = 0; i < jobTitleList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setTitle(jobTitleList.get(i).getTextContent());
			job.setName(job.getTitle());
			job.setUrl(jobTitleList.get(i).getAttribute("href"));
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;
				log.info("Failed to parse job details: " + jobTitleList.get(i).getAttribute("href"), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws MalformedURLException, IOException, InterruptedException {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			HtmlElement jobE = page.getFirstByXPath(JOB_SPEC_PATH);
			job.setSpec(jobE.getTextContent());
			
			jobE = page.getFirstByXPath(JOB_ITEMS_PATH+ "li[1]");
			if(jobE != null)
			job.setLocation(jobE.getTextContent());
			
			jobE = page.getFirstByXPath(JOB_ITEMS_PATH + "li[2]");
			if(jobE != null)
			job.setCategory(jobE.getTextContent());
			
			jobE = page.getFirstByXPath(JOB_ITEMS_PATH + "li[3]");
			if(jobE != null)
			job.setType(jobE.getTextContent());
			
			jobE = page.getFirstByXPath(JOB_ITEMS_PATH + "li[4]/i[2]");
			if(jobE != null)
			job.setReferenceId(jobE.getTextContent());
			return job;
		} catch (FailingHttpStatusCodeException e) {
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
