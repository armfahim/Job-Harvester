package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Airbnb<br> 
 * URL: https://careers.airbnb.com/positions/
 * 
 * @author tohedul.islum
 * @since 2019-03-10
 *
 */
@Slf4j
@Service
public class Airbnb extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AIRBNB;
	private String baseUrl;
	private static WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getChromeClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 26);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		List<HtmlElement> jobList = page.getByXPath("//li[@class='jobs-board__positions__list__item']");
		expectedJobCount = jobList.size();
		for (HtmlElement li : jobList) {
			HtmlElement link = li.getElementsByTagName("a").get(0);
			Job job = new Job(link.getAttribute("href"));
			job.setTitle(link.asText());
			job.setName(link.asText());
			job.setLocation(li.getElementsByTagName("span").get(0).asText());
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = webClient.getPage(job.getUrl());
			List<HtmlElement> spec = page.getByXPath("//div[@class='page-positions__overview active']");
			job.setSpec(spec.get(0).asText());
			job.setApplicationUrl(job.getUrl() + "/?application");
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse detail page data of" + job.getUrl() + e);
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
