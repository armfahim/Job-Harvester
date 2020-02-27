package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
 * CapitalOne job site parsing class. <br>
 * URL: https://campus.capitalone.com/search-jobs
 * 
 * @author tanmoy.tushar
 * @since 2019-02-12
 */
@Service
@Slf4j
public class CapitalOne extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CAPITAL_ONE_FINANCIAL;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM. dd, yyyy");

	private static WebClient client = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws FailingHttpStatusCodeException, IOException, PageScrapingInterruptedException{
		try {
			this.baseUrl = site.getUrl().substring(0, 29);
			HtmlPage page = client.getPage(site.getUrl());
			List<HtmlElement> jobListE = page.getBody().getByXPath("//section[@id='search-results-list']/ul/li/a");
			expectedJobCount = jobListE.size();
			for (HtmlElement row : jobListE) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(baseUrl + row.getAttribute("href"));
				HtmlElement titleE = row.getElementsByTagName("h2").get(0);
				job.setTitle(titleE.getTextContent());
				job.setName(job.getTitle());
				try {
				saveJob(getJobDetail(job), site);
				}catch(Exception e) {
					exception = e;
				}
			}

		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(SITE + " failed to connect site", e);
			throw e;
		}
	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = client.getPage(job.getUrl());

			HtmlElement el1 = page.getBody().getFirstByXPath("//span[@class='job-id job-info']");
			job.setReferenceId(el1.getTextContent().substring(8).trim());

			el1 = (HtmlElement) page.getBody().getFirstByXPath("//span[@class='job-date job-info']");
			if (el1 != null)
				job.setPostedDate(parseDate(el1.getTextContent().substring(12), DF));
			if (job.getPostedDate() == null) log.info(" failed to parse date value " + el1.getTextContent() + " for job " + job.getUrl());

			el1 = (HtmlElement) page.getBody().getFirstByXPath("//span[@class='job-info']");
			job.setLocation(el1.getTextContent().substring(9).trim());

			el1 = (HtmlElement) page.getBody().getFirstByXPath("//div[@class='ats-description']");
			job.setSpec(el1.getTextContent());

			el1 = (HtmlElement) page.getBody().getFirstByXPath("//a[@class='button job-apply top']");
			job.setApplicationUrl(el1.getAttribute("href"));
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse job" + job.getUrl(), e);
		}
		return job;
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
