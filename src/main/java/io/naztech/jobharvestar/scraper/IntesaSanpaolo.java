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
 * Intesa Sanpaolo job site Parser. <br>
 * URL: https://jobs.intesasanpaolo.com/search/?createNewAlert=false&q&locationsearch&locale=en_US
 * 
 * @author armaan.choudhury
 * @since 2019-01-30
 */
@Service
@Slf4j
public class IntesaSanpaolo extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INTESA_SANPAOLO;
	private String baseUrl;
	private static final String url = "https://jobs.intesasanpaolo.com";
	private static final int JOBLIST_OFFSET = 25;
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.INTESA_SANPAOLO));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 39);
		int totalPages = getTotalPages(siteMeta);
		for (int i = 1; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			if (log.isDebugEnabled()) log.debug("Scrapping on Page " + i);
			String nextPage = this.baseUrl + "&startrow=" + (i * JOBLIST_OFFSET - JOBLIST_OFFSET);
			getSummaryPage(siteMeta, nextPage);
		}
	}

	private int getTotalPages(SiteMetaData siteMeta) throws IOException {
		try {
			Document doc = Jsoup.connect(siteMeta.getUrl()).get();
			Element element = doc.selectFirst("span.paginationLabel");
			String totalJob = element.text().split("of")[1].trim();
			expectedJobCount = Integer.parseInt(totalJob);
			return getPageCount(totalJob, JOBLIST_OFFSET);
		} catch (IOException e) {
			log.warn(" failed to parse total page count" + e);
			throw e;
		}
	}

	private void getSummaryPage(SiteMetaData siteMeta, String next) throws InterruptedException {
		String jobUrl = "";
		try {
			Elements list = Jsoup.connect(next).get().select("div.searchResultsShell > table > tbody > tr > td > span > a");
			for (Element el : list) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				jobUrl = url + el.attr("href");
				Job job = new Job(jobUrl);
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (Exception e) {
			log.warn("Failed to parse job list page of " + jobUrl, e);
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[itemprop=datePosted]");
		if(jobE != null) job.setPostedDate(parseDate(jobE.text().trim(), DF1, DF2));
		jobE = doc.selectFirst("span[itemprop=jobLocation]");
		if(jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("a[class=btn btn-primary btn-large btn-lg apply dialogApplyBtn ]");
		if(jobE != null) job.setApplicationUrl(url + jobE.attr("href"));
		jobE = doc.selectFirst("span[class=jobdescription]");
		job.setSpec(jobE.text());
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
