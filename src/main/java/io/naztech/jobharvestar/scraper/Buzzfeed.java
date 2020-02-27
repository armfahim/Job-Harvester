package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Buzzfeed job parsing class<br>
 * URL: https://www.buzzfeed.com/about/jobs
 * 
 * @author BM Al-Amin
 * @author iftekar.alam
 * @since 2019-03-13
 */
@Slf4j
@Service
public class Buzzfeed extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BUZZFEED;
	private String baseUrl;
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		client = getFirefoxClient();
		HtmlPage page = client.getPage(site.getUrl());
		List<HtmlElement> jobList = page.getByXPath("//div[@class='xs-mb2 lg-pl4']/a");
		expectedJobCount = jobList.size();
		for (HtmlElement el : jobList) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.select("div > h1.app-title").get(0).text().trim());
		job.setName(job.getTitle());
		job.setLocation(doc.select("div.location").get(0).text().trim());
		job.setSpec(doc.select("div#content").get(0).text().trim());
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