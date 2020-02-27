package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

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
 * Medibank Private job site parser. <br>
 * URL: https://www.seek.com.au/Medibank-jobs
 * 
 * @author benajir.ullah
 * @author tanmoy.tushar
 * @since 2019-02-13
 */
@Slf4j
@Service
public class MedibankPrivate extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MEDIBANK_PRIVATE;
	private static final int JOBS_PER_PAGE = 20;
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("d MMM yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.MEDIBANK_PRIVATE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 23);
		Document doc = loadPage(site.getUrl());
		int totalPages = getTotalPage(doc);
		browseJobList(doc, site);
		for (int i = 2; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = site.getUrl() + "?page=" + i;
			try {
				doc = loadPage(url);
				browseJobList(doc, site);				
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Document doc, SiteMetaData site) throws InterruptedException {
		Elements jobList = doc.select("a[data-automation=jobTitle]");
		Elements jobLoc = doc.select("a[data-automation=jobLocation]");
		Elements jobCat = doc.select("a[data-automation=jobClassification]");
		for (int i = 0; i < jobList.size(); i++) {
			Job job = new Job(getBaseUrl() + jobList.get(i).attr("href").split("type")[0].trim());
			job.setTitle(getCorrectTitle(jobList.get(i).text()));
			job.setName(job.getTitle());
			job.setLocation(jobLoc.get(i).text().trim());
			job.setCategory(jobCat.get(i).text().trim());
			try {
				saveJob(getJobDetail(job), site);				
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = loadPage(job.getUrl());
		Element jobE = doc.selectFirst("div[class=templatetext]");
		if (jobE == null) jobE = doc.selectFirst("div[data-automation=jobDescription]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("dd[data-automation=job-detail-date]");
		job.setPostedDate(parseDate(getPostedDate(jobE.text()).trim(), DF1, DF2));
		jobE = doc.selectFirst("dd[data-automation=job-detail-work-type]");
		job.setType(jobE.text().trim());
		jobE = doc.selectFirst("a[data-automation=job-detail-apply]");
		job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		return job;
	}
	
	private String getPostedDate(String txt) {
		String[] parts = txt.split(" ");
		if (parts.length > 2 && parts[1].length() > 3) {
			String month = parts[1].substring(0,3);
			return parts[0] + " " + month + " " + parts[2];
		}
		return txt;		
	}
	
	private String getCorrectTitle(String txt) {
		if (txt.contains("-")) return txt.split("-")[0].trim();
		return txt;
	}

	private int getTotalPage(Document doc) {
		Element el = doc.selectFirst("strong[data-automation=totalJobsCount]");
		String totalJob = el.text().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBS_PER_PAGE);
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
