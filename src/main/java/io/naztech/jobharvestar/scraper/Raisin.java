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
 * Raisin job site parser<br>
 * URL: https://www.raisin.com/careers/
 * 
 * @author Arifur Rahman
 * @author jannatul.maowa
 * @since 2019-03-23
 */
@Service
@Slf4j
public class Raisin extends AbstractScraper implements Scrapper {
	private String baseUrl;
	private static final String SITE = ShortName.RAISIN;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document document = Jsoup.connect(siteMeta.getUrl()).get();
		Elements jobE = document.select("a[class=job-item]");
		expectedJobCount = jobE.size();
		for (int i = 0; i < jobE.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(jobE.get(i).attr("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) {
		try {
			Document document = Jsoup.connect(job.getUrl()).get();
			Element jobE = document.selectFirst("div[class= col-sm-10 job-detail-desc]").selectFirst("h1");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = document.selectFirst("div[class= col-sm-10 job-detail-desc]").selectFirst("p");
			job.setType(jobE.text().split("\u00B7")[0].trim());
			job.setLocation(jobE.text().split("\u00B7")[1].trim());
			job.setApplicationUrl(job.getUrl() + "#apply");
			jobE = document.selectFirst("div[class=inner]");
			job.setPrerequisite(jobE.text());
			jobE = document.selectFirst("div[class=col-md-6 pull-right]");
			job.setSpec(jobE.text());
			jobE = document.selectFirst("div[class=col-md-6]");
			job.setSpec(job.getSpec() + " " + jobE.text());
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse JobDetails" + job.getUrl() + e);
			return null;
		}
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
