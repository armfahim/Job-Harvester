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
 * Yoco job site scraper. <br>
 * URL: https://www.yoco.co.za/za/careers/
 * 
 * @author muhammad.tarek
 * @since 2019-04-02
 */
@Slf4j
@Service
public class Yoco extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.YOCO;
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
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 22);
		getSummaryPage(site.getUrl(), site);
	}

	private void getSummaryPage(String url, SiteMetaData site) throws InterruptedException, FailingHttpStatusCodeException, IOException {
		HtmlPage page = client.getPage(url);
		List<HtmlElement> el = page.getByXPath("//div[@class='row _1l8dmqu']");
		expectedJobCount = el.size();
		for (HtmlElement tr : el) {
			if (isStopped()) throw new PageScrapingInterruptedException();

			HtmlElement title = tr.getElementsByTagName("a").get(0);
			Job job = new Job(getBaseUrl() + title.getAttribute("href"));

			job.setTitle(title.asText());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			HtmlElement spec = page.getFirstByXPath("//div[@class='col-md-7 jobListing']");
			job.setSpec(spec.asText());
			job.setLocation(spec.getElementsByTagName("h2").get(0).asText());
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info(getSiteName() + "Failed to parse job details", e);
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
