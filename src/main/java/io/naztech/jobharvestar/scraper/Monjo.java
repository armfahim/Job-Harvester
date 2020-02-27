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
 * monzo job site scraper. <br>
 * URL: https://monzo.com/careers/#jobs
 * 
 * @author Asadullah Galib
 * @since 2019-03-12
 */
@Slf4j
@Service
public class Monjo extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MONZO;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 31);
		getSummaryPages(getBaseUrl(), siteMeta);
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws IllegalStateException, InterruptedException, IOException {
		try {
			HtmlPage page;
			page = client.getPage(siteMeta.getUrl());
			client.waitForBackgroundJavaScript(5000);
			List<HtmlElement> jobLinks = page.getBody().getByXPath("//div[@class='grid-row margin-top-none']/a");
			expectedJobCount = jobLinks.size();
			for (HtmlElement ab : jobLinks) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(ab.getAttribute("href"));
				job.setTitle(ab.getElementsByTagName("span").get(0).asText());
				try {
				saveJob(getJobDetails(job), siteMeta);
				}catch(Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse Site: " + getSiteName(), e);
			throw e;
		}
	}

	private Job getJobDetails(Job job) {
		HtmlPage page;
		try {
			page = client.getPage(job.getUrl());
			client.waitForBackgroundJavaScript(10000);
			HtmlElement spec = (HtmlElement) page.getElementById("content");
			job.setSpec(spec.asText());

		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parsed job details of " + job.getUrl(), e);
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
