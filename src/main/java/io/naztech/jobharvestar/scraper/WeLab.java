package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * WeLab jobsite pareser.<br>
 * URL: https://www.welab.co/en/careers
 * 
 * @author Rahat Ahmad
 * @since 2019-04-01
 */
@Slf4j
@Service
public class WeLab extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.WELAB;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 20);
		List<Job> jobList = getSummaryPage(siteMeta.getUrl());
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			try {
				saveJob(getJobDetails(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
			Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_4S));
		}
	}

	private List<Job> getSummaryPage(String url) throws IOException {
		Document document = Jsoup.connect(url).get();
		Elements jobEl = document.select("a.button");
		List<Job> jobList = new ArrayList<>();
		for (int i = 1; i<jobEl.size();i++) {
			document = Jsoup.connect(getBaseUrl()+jobEl.get(i).attr("href")).get();
			Elements jobEl1= document.select("div.wrapper:has(h2.title)");
			for(int j=0;j<jobEl1.size();j++) {
				String category = jobEl1.get(j).select("h2.title").text();
				Elements jobLinksE = jobEl1.get(j).select("a");
				for (int k = 0; k < jobLinksE.size(); k++) {
					Job job = new Job();
					job.setCategory(category);
					job.setUrl(getBaseUrl()+jobLinksE.get(k).attr("href"));
					job.setTitle(jobLinksE.get(k).select("span").text());
					job.setName(job.getTitle());
					jobList.add(job);
				}
			}
		}
		return jobList;
	}

	private Job getJobDetails(Job job) throws IOException {
		Document jobDetailsPage = Jsoup.connect(job.getUrl()).get();
		try {
			job.setSpec(jobDetailsPage.select("div.job:has(div.description)").text());
		} catch (NullPointerException e) {
			log.warn("Spec not found", e);
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
