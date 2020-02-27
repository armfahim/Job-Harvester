package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
/**
 * APUS GROUP Job Site Parser </br>
 * URL: http://www.apusapps.com/en/jobs/
 * 
 * @author Rahat Ahmad
 * @author bm.alamin
 * @since 2019-03-11
 * 
 */
@Service
public class Apus extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.APUS_GROUP;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document summaryPage = Jsoup.connect(siteMeta.getUrl()).get();
		this.baseUrl = siteMeta.getUrl();
		Elements jobListE = summaryPage.select("div.join-us-layer > div > div > ul");
		expectedJobCount = jobListE.size();
		for (int i = 0; i < jobListE.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(jobListE, i), siteMeta);		
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Elements jobListE, int i) {
		Job job = new Job();
		job.setTitle(jobListE.get(i).select("h4").text());
		job.setName(job.getTitle());
		job.setSpec(jobListE.get(i).text());
		job.setUrl(getBaseUrl() + getJobHash(job));
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
