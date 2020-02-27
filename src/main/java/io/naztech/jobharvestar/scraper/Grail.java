package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
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
 * Grail jobsite pareser <br>
 * Url: https://grail.com/careers/career-listings/
 * 
 * @author Rahat Ahmad
 * @since 2019-03-12
 */
@Service
public class Grail extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.GRAIL;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 17);
		List<Job> jobList = getSummaryPage(siteMeta.getUrl());
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
			saveJob(getJobDetails(job), siteMeta);
			}catch(Exception e) {
				exception = e;
			}
			Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_4S));
		}
	}

	private List<Job> getSummaryPage(String url) throws PageScrapingInterruptedException, IOException {
		Document summaryPage = Jsoup.connect(url).get();
		Elements jobsE = summaryPage.select("section.row-listing > div");
		List<Job> jobList = new ArrayList<Job>();
		for (int i = 0; i < jobsE.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setApplicationUrl(jobsE.get(i).select("a").get(0).attr("href"));
			job.setUrl(getBaseUrl() + jobsE.get(i).select("a").get(1).attr("href"));
			job.setTitle(jobsE.get(i).select("h2").text());
			job.setName(job.getTitle());
			job.setCategory(jobsE.get(i).select("span").get(0).text().replace(",", "").trim());
			job.setLocation(jobsE.get(i).select("span").get(1).text());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) throws IOException {
		Document document = Jsoup.connect(job.getUrl()).get();
		String spec = null;
		spec = document.select("main.main > section").get(2).select("div").text();
		if (spec == null)
			return null;
		job.setSpec(spec);
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
