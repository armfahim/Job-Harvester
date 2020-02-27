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
 * GALLAGHER (ARTHUR J.)<br>
 * URL: https://jobs.ajg.com/jobs/search/
 * 
 * @author Jubayer Ahmed Riad
 * @author tanmoy.tushar
 * @since 2019-02-13
 */
@Slf4j
@Service
public class Gallagher extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.GALLAGHER_ARTHUR_J;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final int JOBS_PER_PAGE = 10;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.GALLAGHER_ARTHUR_J));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc = loadPage(site.getUrl());
		Elements rowList = doc.select("a[class=job_link font_bold]");
		browseJobList(rowList, site);
		Element jobUrl = doc.selectFirst("a[class=link_title font_bold jSearchLink]");
		this.baseUrl = jobUrl.attr("href");
		int totalPage = getTotalPage(doc);
		for (int i = 2; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/page" + i;
			try {
				doc = loadPage(url);
				rowList = doc.select("a[class=job_link font_bold]");
				browseJobList(rowList, site);			
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = el.attr("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = loadPage(job.getUrl());
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=job_description]").text().trim());
		Element jobE = doc.selectFirst("h4");
		job.setLocation(jobE.text().substring(2).trim());
		jobE = doc.selectFirst("span[class=field_value font_header_light]");
		job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("dd[class=job_external_id]>span");
		job.setReferenceId(jobE.text().trim());
		jobE = doc.selectFirst("dd[class=job_post_date]>span");
		if (jobE != null) {
			String date = jobE.text().trim();
			if (date.contains("ago")) job.setPostedDate(parseAgoDates(date));
			else job.setPostedDate(parseDate(date, DF));
		}
		return job;
	}
	
	private int getTotalPage(Document doc) {
		String totalJob = doc.selectFirst("span[class=total_results]").text().trim();
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
