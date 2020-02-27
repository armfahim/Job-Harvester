package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
 * S&amp;P Global job site. <br>
 * URL: https://careers.spglobal.com/ListJobs/All
 * 
 * @author naym.hossain
 * @since 2019-01-22
 */
@Slf4j
@Service
public class SnpGlobal extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SNP_GLOBAL;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	private static final String HEADURL = "/ListJobs/All/Page-";
	private static final int JOBPERPAGE = 30;
	private String baseUrl;
	private DateTimeFormatter ft = DateTimeFormatter.ofPattern("M-d-yyyy");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.SNP_GLOBAL));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 28);
		int totalPages = getTotalPage(siteMeta.getUrl());
		for (int i = 1; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPage(getBaseUrl() + HEADURL + i, siteMeta);
		}
	}

	private int getTotalPage(String url) throws IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		if (doc == null) throw new IOException(SITE + " failed to parse page count");
		Elements elTotalPageNo = doc.select("span.pager_counts");
		String totalJob = elTotalPageNo.get(0).text().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBPERPAGE);
	}

	private void getSummaryPage(String url, SiteMetaData siteMeta) throws InterruptedException {
		try {
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
			if (doc == null) throw new IOException(SITE + " failed to fetch summary page " + url);
			Elements list = doc.select("table.JobListTable > tbody > tr:gt(1)");
			for (Element el : list) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				String joburl = getBaseUrl() + el.child(0).child(0).attr("href");
				Job job = new Job();
				job.setUrl(joburl);
				job.setCategory(el.child(2).text());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to collect job list of " + url, e);
		}		
	}

	private Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			if (doc == null) throw new IOException("Failed to fetch job details page " + job.getUrl());

			job.setTitle(doc.select("div.pageHeader").get(0).text());
			job.setName(job.getTitle());
			job.setReferenceId(doc.select("div.jobid > div.jobdescription-value").get(0).text());
			try {
				job.setPostedDate(parseDate(doc.select("div.addedon > div.jobdescription-value").get(0).text(), ft));
				if (job.getPostedDate() == null) log.info(" failed to parse date value " + doc.select("div.addedon > div.jobdescription-value").get(0).text() + " for job " + job.getUrl());
			} catch (DateTimeParseException e) {
				log.warn(SITE + " failed to parse date", e);
			}
			job.setLocation(doc.select("div.location > div.jobdescription-value").get(0).text());
			job.setSpec(doc.select("div.jobdescription-value").get(0).wholeText());
			job.setApplicationUrl(doc.select("div.applyBtnBottomDiv > a").get(0).attr("href"));

			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " +job.getUrl(), e);
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
