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
 * Macquire Group.<br>
 * URL: http://www.careers.macquarie.com/cw/en/listing/
 * 
 * @author naym.hossain
 * @since 2019-01-22
 */
@Slf4j
@Service
public class MacquireGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MACQUAIRE_GROUP;
	private String baseUrl;
	private static final String HEADURL = "/cw/en/listing/?page=";
	private static final String TAILURL = "&page-items=20";
	private static final int JOBS_PER_PAGE = 20;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.MACQUAIRE_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 32);
		int totalPage = getTotalPages(siteMeta.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPages(getBaseUrl() + HEADURL + i + TAILURL, siteMeta);
		}
	}

	private int getTotalPages(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements elTotalPageNo = doc.select("div#recent-jobs > p:has(a.more-link) > a:has(span)");
		String totalJob = elTotalPageNo.get(0).child(0).text();
		expectedJobCount = Integer.parseInt(totalJob) + JOBS_PER_PAGE;
		return getPageCount(totalJob, JOBS_PER_PAGE);
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		try {
			Document doc = Jsoup.connect(url).get();
			Elements list = doc.select("table > tbody#recent-jobs-content > tr > td > a.job-link");
			for (Element link : list) { 
				if (isStopped()) throw new PageScrapingInterruptedException();
				String jobrul = getBaseUrl() + link.attr("href");
				try {
				saveJob(getJobDetail(jobrul), siteMeta);
				}catch(Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to collect job list of " + url, e);
		}		
	}

	private Job getJobDetail(String url) throws InterruptedException {
		try {
			Job job = new Job();
			Document doc = Jsoup.connect(url).get();
			Elements elJob = doc.select("div#job-content");
			job.setName(elJob.get(0).child(0).text());
			job.setTitle(elJob.get(0).child(0).text());
			Elements elJob2 = doc.select("div#job-content > p:first-of-type > span");
			for (Element el : elJob2) {
				if (isStopped()) throw new PageScrapingInterruptedException();		
				if (el.className().contains("job-externalJobNo")) job.setReferenceId(el.text());			
				else if (el.className().contains("work-type agency-worker")) job.setType(el.text());		
				else if (el.className().contains("location")) job.setLocation(el.text());
			}
			
			Elements elJob3 = doc.select("div#job-content > p:first-of-type");
			job.setComment(elJob3.get(0).ownText());
			job.setApplicationUrl(elJob3.get(0).child(0).child(0).attr("href"));
			Elements elDes = doc.select("div#job-details");
			job.setSpec(elDes.get(0).wholeText().trim());
			job.setUrl(url);
			Elements date = doc.select("div#job-content > p:has(span.open-date) > span.open-date");
			job.setPostedDate(parseDate(date.get(0).text(), DF));
			if (job.getPostedDate() == null)
				log.warn(" failed to parse date value " + date.get(0).text() + " for job " + job.getUrl());
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + url, e);
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
