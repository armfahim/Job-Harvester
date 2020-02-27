package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Slack Technologies<br>
 * URL: https://slack.com/careers#openings
 * 
 * @author bm.alamin
 * @author armaan.choudhury
 * @since 2019-03-14
 */
@Service
@Slf4j
public class SlackTechnologies extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SLACK_TECHNOLOGIES;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.SLACK_TECHNOLOGIES));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		getSummaryPage(siteMeta);
	}

	private void getSummaryPage(SiteMetaData siteMeta) throws InterruptedException, IOException {
		try {
			Elements list = Jsoup.connect(siteMeta.getUrl()).get()
					.select("tbody > tr.c-filter-items > td > a.link-careers-apply");
			expectedJobCount = list.size();
			System.out.println("Total job: " + expectedJobCount);
			for (Element el : list) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetail(el.attr("href")), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Exception in getSummaryPage: " + e);
			throw e;
		}
	}

	private Job getJobDetail(String url) throws InterruptedException {
		Job job = new Job(url);
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			job.setTitle(doc.select("div.career-details-description > h3.u-text--center").text());
			job.setName(job.getTitle());
			job.setLocation(doc.select("div.career-detail-nav-secondary > span.career-deatil-location > p").text());
			job.setApplicationUrl(job.getUrl());
			job.setSpec(doc.select("div.career-details-description > p").text());
			job.setPrerequisite(doc.select("div.career-details-description > ul > li > span").text());
			return job;
		} catch (IOException e) {
			log.warn("Failed to get job details" + e);
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
