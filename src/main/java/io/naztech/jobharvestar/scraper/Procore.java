package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Procore <br>
 * URL: https://www.procore.com/jobs/openings
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-03-13
 */
@Service
@Slf4j
public class Procore extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PROCORE_TECHNOLOGIES;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));

	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements jobList = doc.select("a[class=link-careers-apply]");
		expectedJobCount = jobList.size();
		for (Element li : jobList) {
			Job job = new Job(getBaseUrl() + li.attr("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h1");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("p[class=location]");
			job.setLocation(jobE.text());
			jobE = doc.selectFirst("div[class=col-lg-8 col-lg-offset-2 col-md-8 col-md-offset-2 col-sm-10 col-sm-offset-1 col-xs-12]");
			job.setSpec(jobE.text());
			jobE = doc.selectFirst("div[id=gtm-jobdetail-desc]");
			job.setApplicationUrl(job.getUrl() + "#greenhouse_container");
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job detail page of " + job.getUrl(), e);
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
