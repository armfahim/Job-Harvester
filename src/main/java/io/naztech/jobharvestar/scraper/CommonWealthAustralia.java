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
 * Common Wealth Bank of Australia.<br>
 * URL: http://careers.commbank.com.au/commbank/en/listing/
 * 
 * @author naym.hossain
 * @author tanmoy.tushar
 * @since 2019-01-22
 */
@Slf4j
@Service
public class CommonWealthAustralia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.COMMONWEALTH_BANK_OF_AUSTRALIA;
	private static final String HEADURL = "/commbank/en/listing/?page=";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final int JOBS_PER_PAGE = 20;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.COMMONWEALTH_BANK_OF_AUSTRALIA));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws InterruptedException, IOException {
		this.baseUrl = siteMeta.getUrl().substring(0, 30);
		int totalPage = getTotalPages(siteMeta.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + HEADURL + i;
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalPages(String url) throws InterruptedException, IOException {
		try {
			Document doc = Jsoup.connect(url).get();
			Element el = doc.selectFirst("a[class=more-link button]>span");
			int totalJobs = JOBS_PER_PAGE + Integer.parseInt(el.text());
			expectedJobCount = totalJobs;
			int totalPage = totalJobs / JOBS_PER_PAGE;
			return totalJobs % JOBS_PER_PAGE == 0 ? totalPage : totalPage + 1;
		} catch (IOException e) {
			log.error("Failed to parse total job ", e);
			throw e;
		}
	}

	public void browseJobList(String url, SiteMetaData siteMeta) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).get();
		Elements elSummary = doc.select("table > tbody#recent-jobs-content > tr > td > a.job-link");
		for (Element link : elSummary) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + link.attr("href"));
			job.setTitle(link.text().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("span[class=job-externalJobNo]");
		if (jobE != null) job.setReferenceId(jobE.text().trim());
		jobE = doc.selectFirst("a[class=apply-link button]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("span[class=open-date]>time");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text().trim(), DF));
		jobE = doc.selectFirst("span[class=location]");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("span[class=categories]");
		if (jobE != null) job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("span[class=work-type permanent-full-time]");
		if (jobE != null) job.setType(jobE.text().trim());
		if (job.getType() == null) {
			jobE = doc.selectFirst("span[class=work-type permanent-part-time]");
			if (jobE != null) job.setType(jobE.text().trim());
		}
		jobE = doc.getElementById("job-details");
		if (jobE == null) {
			jobE = doc.getElementById("job-inner-content");
			job.setSpec(jobE.text().trim());
		}
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
