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
 * Payoneer job site parser.<br>
 * URL: https://www.payoneer.com/careers/
 * 
 * @author Md. Sanowar Ali
 * @since 2019-04-02
 */
@Service
@Slf4j
public class Payoneer extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PAYONNER;
	private String baseUrl;
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.PAYONNER));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl();
		client = getFirefoxClient();
		getSummaryPages(getBaseUrl(), siteMeta);
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws PageScrapingInterruptedException, IOException {
		try {
			HtmlPage page = client.getPage(url);
			client.waitForBackgroundJavaScript(5 * 1000);
			List<HtmlElement> list = page.getByXPath("//div[@class='job-item visible']/a");
			expectedJobCount = list.size();
			for (HtmlElement row : list) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(row.getAttribute("href"));
				try {
					saveJob(getJobDetails(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.info("Failed to parse job Summary Pages " + url, e);
			throw e;
		}
	}

	public Job getJobDetails(Job job) {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			client.waitForBackgroundJavaScript(5 * 1000);
			HtmlElement elSpec = page.getFirstByXPath("//p[@class='pos-desc']");
			job.setSpec(elSpec.asText().trim());
			HtmlElement title = page.getFirstByXPath("//h1[@class='pos-title']");
			job.setTitle(title.getTextContent().trim());
			job.setName(job.getTitle());
			HtmlElement location = page.getFirstByXPath("//p[@class='pos-loc']");
			job.setLocation(location.getTextContent().trim());
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info("Failed to parse job details " + job.getUrl(), e);
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
