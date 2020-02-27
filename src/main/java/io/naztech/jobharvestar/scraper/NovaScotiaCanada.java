package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Nova Scotia Canada jon site parser. <br>
 * URL: https://jobs.scotiabank.com/search/?q=&sortColumn=referencedate&sortDirection=desc
 * 
 * @author tohedul.islum
 * @since 2019-02-04
 */
@Service
@Slf4j
public class NovaScotiaCanada extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANK_NOVA_SCOTIA_CANADA;
	private String baseUrl;
	private static final String TAILURL = "/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=";
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));

	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 27);
		int totalJob = getTotalJobs(siteMeta.getUrl());
		for (int i = 0; i < totalJob; i += 25) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + TAILURL + i;
			try {
				getSummaryPages(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalJobs(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		Document doc = Jsoup.connect(url).get();
		Element el = doc.selectFirst("span[class=paginationLabel]");
		String totalJob = el.text().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getExpectedJob();
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("div[class=jobdetail-phone visible-phone]>span>a");
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.attr("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[itemprop=datePosted]");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text().trim(), DF1, DF2));
		jobE = doc.selectFirst("span[itemprop=jobLocation]");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("span[itemprop=description]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("button[class=btn btn-primary btn-large btn-lg dropdown-toggle]");
		if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		else {
			jobE = doc.selectFirst("a[class=btn btn-primary btn-large btn-lg apply dialogApplyBtn ]");
			if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		}
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
