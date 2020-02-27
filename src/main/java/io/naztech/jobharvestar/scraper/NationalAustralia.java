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
 * National Australia Bank Job Site Scraper. <br>
 * URL: http://careers.nab.com.au/aw/en/listing/?page=1
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-01-16
 */
@Slf4j
@Service
public class NationalAustralia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NATIONAL_AUSTRALIA_BANK;
	private String baseUrl;
	private DateTimeFormatter ft = DateTimeFormatter.ofPattern("dd MMM yyyy h:mm a");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.NATIONAL_AUSTRALIA_BANK));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 25);
		int totalPage = getTotalPage(site.getUrl());
		for (int page = 1; page <= totalPage + 1; page++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/aw/en/listing/?page=" + page;
			try {
				browseJobList(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements elements = doc.select("tbody[id=recent-jobs-content]>tr>td>a");
		for (Element el : elements) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.attr("href"));
			job.setTitle(el.text().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}
	
	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h3");
		jobE = doc.selectFirst("a[class=apply-link button]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("span[class=job-externalJobNo]");
		if (jobE != null) job.setReferenceId(jobE.text());
		jobE = doc.selectFirst("span[class=location]");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("span.work-type");
		if (jobE != null) job.setType(jobE.text().trim());
		jobE = doc.selectFirst("span[class=open-date]");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text(), ft));
		jobE = doc.selectFirst("div[class=jobcon]");
		if (jobE == null) {
			jobE = doc.selectFirst("div[id=summary]");
			if (jobE == null) jobE = doc.selectFirst("div[id=job-content]");
		}
		job.setSpec(jobE.text().trim());
		return job;
	}

	private int getTotalPage(String url) throws IOException {
		try {
			Document doc = Jsoup.connect(url).get();
			Element el = doc.selectFirst("a[class=more-link button]>span");
			String totalJob = el.text().trim();
			expectedJobCount = Integer.parseInt(totalJob) + 15;
			return getPageCount(totalJob, 15);
		} catch (IOException e) {
			log.error("Failed to parse total job, site exiting...", e);
			throw e;
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
