package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Houzz jobsite pareser.<br>
 * URL: https://www.houzz.com/jobs#career
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-03-12
 */
@Slf4j
@Service
public class Houzz extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.HOUZZ;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 21);
		List<String> jobsTypeE = getAllTypesOfjob(siteMeta.getUrl());
		for (String string : jobsTypeE) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<String> jobsUrl = getSummaryPages(string);
			if (jobsUrl == null) continue;
			expectedJobCount += jobsUrl.size();
			for (String jobUrl : jobsUrl) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(jobUrl), siteMeta);					
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + jobUrl, e);
				}
			}
		}
	}

	private List<String> getSummaryPages(String url) throws IOException {
		Document document = loadPage(url);
		List<String> urls = new ArrayList<>();
		try {
			Elements jobs = document.select("table.jobs-table > tbody > tr:has(td) > td:has(a) > a");
			for (int i = 0; i < jobs.size(); i += 3) {
				if (jobs.get(i).attr("href").isEmpty())
					i++;
				urls.add(getBaseUrl() + jobs.get(i).attr("href"));
			}
		} catch (NullPointerException e) {
			log.warn("Job List not found", e);
			urls = null;
		}
		return urls;
	}

	private Job getJobDetails(String jobUrl) throws IOException {
		Document document = loadPage(jobUrl);
		Job job = new Job(jobUrl);
		job.setTitle(document.select("div.job-content > div").get(0).text());
		job.setLocation(document.select("div.job-content > div").get(1).text());
		job.setName(job.getTitle());
		job.setSpec(document.select("div.job-content").text());
		job.setApplicationUrl(document.select("div.job__applyButton > a").attr("href"));
		return job;
	}

	private List<String> getAllTypesOfjob(String url) throws IOException {
		Document document = loadPage(url);
		Elements jobEl = document.select("div#filter-by-dep > a");
		List<String> jobUrl = new ArrayList<>();
		for (int i = 0; i < jobEl.size(); i++) {
			jobUrl.add(getBaseUrl() + jobEl.get(i).attr("href"));
		}
		return jobUrl;
	}
	
	private Document loadPage(String url) throws IOException {
		return Jsoup.connect(url).get();
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
