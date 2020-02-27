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
 * MSCI Inc. job site parser. <br>
 * URL: https://careers.msci.com/ListJobs/All/Page-
 * 
 * @author farzana.islam
 * @author iftekar.alam
 * @since 2019-01-23
 */
@Slf4j
@Service
public class MsciCapital extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MSCI;
	private String baseUrl;
	private static final String JOBSITE_HOST = "https://careers.msci.com";
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws InterruptedException, IOException {
		int totalPages = getTotalPage(siteMeta.getUrl());
		for (int i = 1; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPage(siteMeta.getUrl() + "/Page-" + i, siteMeta);
		}
	}

	private int getTotalPage(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		String pfc = doc.select("span[class=pager_counts]").text().split("of")[1].trim().split("Showing")[0].trim();
		expectedJobCount = Integer.parseInt(pfc);
		return getPageCount(pfc, 30);
	}

	private void getSummaryPage(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements list = doc.select("table.JobListTable > tbody > tr:not(th)");
		for (Element el : list) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Elements joblinks = el.select("td.coloriginaljobtitle > a");
			if (joblinks == null || joblinks.isEmpty()) continue;
			Job job = new Job(JOBSITE_HOST + joblinks.get(0).attr("href"));
			job.setTitle(joblinks.get(0).text());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.select("div.descrip").get(0).wholeText());
		job.setType(doc.select("div#col2").get(0).child(2).wholeText());
		job.setReferenceId(doc.select("div#col2").get(0).child(0).wholeText());
		job.setLocation(doc.select("div#col2").get(0).child(1).wholeText());
		job.setCategory(doc.select("div#col2").get(0).child(3).wholeText());
		job.setApplyEmail(doc.select("a.sf_applybtn").get(0).attr("href"));
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
