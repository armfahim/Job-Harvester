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
import lombok.extern.slf4j.Slf4j;

/**
 * CircleUp jobsite parser URL: https://circleup.com/jobs/#job-listings
 * 
 * @author rafayet.hossain
 * @since 2019-04-01
 */
@Slf4j
@Service
public class CircleUp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CIRCLEUP;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 20);
		Document document = Jsoup.connect(siteMeta.getUrl()).get();
		Job job = new Job();
		Elements el = document.select("div[class=col col-md-4 col-job]");
		expectedJobCount = el.size();
		for (int i = 0; i < el.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Elements title = el.get(i).select("h6[class=job__title]");
			Elements location = el.get(i).select("h6[class=job__location]");
			Elements DetailsPageUrl = el.get(i).select("a[class=job d-block]");
			job.setTitle(title.text());
			job.setName(job.getTitle());
			job.setLocation(location.text());
			job.setUrl(baseUrl + DetailsPageUrl.attr("href"));
			try {
			saveJob(getJobDetails(job), siteMeta);
			}catch(Exception e) {
				exception = e;
			}
		}
	}

	public Job getJobDetails(Job job) {
		Document document;
		try {
			document = Jsoup.connect(job.getUrl()).get();
			Elements description = document.select("div[class=job__full-description]");
			Elements applyUrl = document.select("a[class=btn btn-primary btn-apply]");
			job.setSpec(description.text());
			job.setApplicationUrl(applyUrl.attr("href"));
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
		}
		return null;
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
