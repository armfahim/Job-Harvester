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
 * Lazard job site scraper. <br>
 * URL: https://lazard.referrals.selectminds.com/
 * 
 * @author asadullah.galib
 * @author iftekar.alam
 * @since 2019-03-07
 */
@Slf4j
@Service
public class LazardAssesManagement extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.LAZARD_ASSET_MANAGEMENT;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException {
		Document doc=Jsoup.connect(site.getUrl()).get();
		Elements jobList = doc.select("div[class=jlr_title]");
		expectedJobCount=jobList.size();
		for (Element el : jobList) {
			Job job=new Job(el.getElementsByTag("p").get(0).children().attr("href"));
			try {
				saveJob(getJobDetail(job), site);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+job.getUrl(),e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).get();
		Element jobE=doc.selectFirst("h1[class=title]");
		job.setTitle(jobE.text().trim());
		job.setName(job.getUrl());
		jobE=doc.selectFirst("h4[class=primary_location]>a");
		job.setLocation(jobE.text().trim().substring(2).trim());
		jobE=doc.selectFirst("span[class=field_value font_header_light]");
		job.setCategory(jobE.text().trim());
		jobE=doc.selectFirst("dd[class=job_external_id]");
		job.setReferenceId(jobE.child(0).text().trim());
		jobE=doc.selectFirst("dd[class=job_post_date]");
		job.setPostedDate(parseAgoDates(jobE.child(0).text().trim()));
		jobE=doc.selectFirst("div[class=job_description]");
		job.setSpec(jobE.text().trim());
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
