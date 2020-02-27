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

/**
 * Bread job site parser.
 * url: https://www.bread.org/careers
 * 
 * @author sohid.ullah
 * @since 2019-03-25
 * 
 **/
@Service
public class Bread extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BREAD;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 21);
		Document document = Jsoup.connect(siteMeta.getUrl()).get();
		List<Job> jobList = browseJobList(document);
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
			saveJob(getJobDetails(job), siteMeta);
			}catch(Exception e) {
				exception = e;
			}

		}
	}

	private List<Job> browseJobList(Document document) throws PageScrapingInterruptedException {
		Elements jobE = document.select("div>h2>a");
		int numberOfJob = jobE.size();
		List<Job> jobList = new ArrayList<Job>();
		for (int i = 0; i < numberOfJob; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setUrl(getBaseUrl() + jobE.get(i).attr("href"));
			job.setTitle(jobE.get(i).text().trim());
			job.setName(job.getTitle());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) throws IOException {
		Document document = Jsoup.connect(job.getUrl()).get();
		job.setSpec(document.select("div[class=field field-name-body]").text());
		job.setApplicationUrl(document.select("div[class=field field-name-body]>p>a").attr("href"));
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