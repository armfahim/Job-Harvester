package io.naztech.jobharvestar.scraper;

import java.io.IOException;

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
 * Intel Corporation jobs site parser. <br>
 * URL: https://jobs.intel.com/ListJobs/All
 * 
 * @author armaan.choudhury
 * @since 2019-3-5
 */
@Service
@Slf4j
public class IntelCorporation extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INTEL_CORPORATION;
	private String baseUrl;
	private String TAIL_URL = "/ListJobs/All/Page-";
	private static final int JOBLIST_OFFSET = 30;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.INTEL_CORPORATION));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		int totalPages = getTotalPages(siteMeta);
		for (int i = 1; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Thread.sleep(5000);
			if (log.isDebugEnabled()) log.debug("Scrapping on Page " + i);
			String nextPage = this.baseUrl + TAIL_URL + i;
			getSummaryPage(siteMeta, nextPage);
		}
	}

	private int getTotalPages(SiteMetaData siteMeta) throws IOException {
		try {
			Document doc = Jsoup.connect(siteMeta.getUrl()).get();
			Elements elements = doc.select("span.pager_counts");
			String totalJob = elements.get(0).text().split("of")[1].trim();
			expectedJobCount = Integer.parseInt(totalJob);
			return getPageCount(totalJob, JOBLIST_OFFSET);
		} catch (IOException e) {
			log.error("Failed to parse total page count" + e);
			throw e;
		}
	}

	private void getSummaryPage(SiteMetaData siteMeta, String next) throws InterruptedException {
		try {
			Elements list = Jsoup.connect(next).get().select("table.JobListTable > tbody > tr");
			for (int i = 2; i < list.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Element el = list.get(i);
				String joburl = this.baseUrl + el.child(0).child(0).attr("href");
				Job job = new Job();
				job.setUrl(joburl);
				job.setTitle(el.child(0).child(0).text());
				job.setName(el.child(0).child(0).text());
				job.setReferenceId(el.child(0).child(0).text().split(" -")[0]);
				String location = el.child(1).text().trim() + ", " + el.child(2).text().trim() + ", "
						+ el.child(3).text().trim();
				job.setLocation(location);
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
				
			}
		} catch (IOException e) {
			log.warn("Failed to parse summary page of " + getSiteName(), e);
		}
	}

	private Job getJobDetail(Job job) {
		try {
			Document doc;
			String jobDescription = null;
			doc = Jsoup.connect(job.getUrl()).get();
			Elements descE = doc.select("div.jobdescriptiontbl");
			jobDescription = descE.text();
			job.setSpec(jobDescription);
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
		}
		return null;
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
