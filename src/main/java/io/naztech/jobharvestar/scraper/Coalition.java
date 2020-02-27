package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
 * Coalition Jobsite Parser <br>
 * URL: https://careers.jobscore.com/careers/coalition/
 * 
 * @author Rahat Ahmad
 * @since 2019-04-02
 */
@Slf4j
@Service
public class Coalition extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.COALITION;
	private String baseUrl = "https://careers.jobscore.com";
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws InterruptedException {
		try {
			Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S).get();
			Elements jobList = doc.select("span[class = js-job-title]>a");
			expectedJobCount = jobList.size();
			for (Element element : jobList) {
				saveJob(getJobDetails(getBaseUrl() + element.attr("href"), doc), siteMeta);
			}
		} catch (Exception e) {
			exception = e;
		}
	}

	private Job getJobDetails(String url, Document doc){
		try {
			Job job = new Job(url);
			doc =  Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_10S).get();
			Element el = doc.selectFirst("h1[class = js-title]");
			job.setTitle(el.text().trim());
			job.setName(job.getTitle());
			
			el = doc.selectFirst("h2[class = js-subtitle]");
			List<String> jobParts = Arrays.asList(el.text().trim().split("|"));
			if(jobParts.size() == 3) {
			job.setCategory(jobParts.get(0).trim());
			job.setLocation(jobParts.get(1).trim());
			job.setType(jobParts.get(2).trim());}
			
			el = doc.selectFirst("a[class = js-btn js-btn-block js-btn-apply]");
			job.setApplicationUrl(getBaseUrl() + el.attr("href"));
			
			el = doc.selectFirst("div[class = js-job-description]");
			job.setSpec(el.text().trim());
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of "+url, e);
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
