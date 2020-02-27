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
 * Brighthouse Financial job site parser. <br>
 * URL: https://jobs.brighthousefinancial.com/search/?searchby=location&createNewAlert=false&q=&locationsearch=&geolocation=
 * 
 * @author Benajir Ullah
 * @author tanmoy.tushar
 * @since 2019-02-12
 */
@Slf4j
@Service
public class Brighthouse extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BRIGHTHOUSE_FINANCIAL;
	private static final int JOBS_PER_PAGE = 25;
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.BRIGHTHOUSE_FINANCIAL));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 37);
		Document doc = loadPage(site.getUrl());
		int totalPages = getTotalPage(doc);
		Elements rowList = doc.select("tr[class=data-row clickable]");
		browseJobList(rowList, site);
		for (int i = 2; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = site.getUrl() + "&startrow=" + ((JOBS_PER_PAGE * i) - JOBS_PER_PAGE);
			try {
				doc = loadPage(url);
				rowList = doc.select("tr[class=data-row clickable]");
				browseJobList(rowList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, SiteMetaData site) throws InterruptedException{
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.selectFirst("a[class=jobTitle-link]").attr("href"));
			job.setTitle(el.selectFirst("a[class=jobTitle-link]").text().trim());
			job.setName(job.getTitle());
			job.setLocation(el.selectFirst("span[class=jobLocation]").text());
			job.setPostedDate(parseDate(el.selectFirst("span[class=jobDate visible-phone]").text(), DF1, DF2));
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = loadPage(job.getUrl());
		job.setSpec(doc.selectFirst("span[class=jobdescription]").text());
		Element jobE = doc.selectFirst("span[itemprop=customfield1]");
		if (jobE != null) job.setReferenceId(jobE.text());
		return job;
	}

	private int getTotalPage(Document doc) {
		String totalJob = doc.select("span[class=paginationLabel]>b").get(1).text();
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
