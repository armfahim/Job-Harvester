package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Citadel job site parser. <br>
 * URL: http://careers.pageuppeople.com/743/cw/en-us/listing
 * 
 * @author tanmoy.tushar
 * @since 2019-03-04
 */
@Service
@Slf4j
public class Citadel extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CITADEL;
	private static final DateTimeFormatter DF_1 = DateTimeFormatter.ofPattern("MMM dd yyyy");
	private static final DateTimeFormatter DF_2 = DateTimeFormatter.ofPattern("MMM d yyyy");
	private static final String ROW_LIST_PATH = "tbody[id=recent-jobs-content]>tr>td>a";
	private static final int JOB_PER_PAGE = 20;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException, IOException {
		this.baseUrl = site.getUrl().substring(0, 31);
		Document doc = loadPage(site.getUrl());
		int totalPage = getTotalPage(doc) + 1;
		Elements rowList = doc.select(ROW_LIST_PATH);
		browseJobList(rowList, site);
		for (int i = 2; i <= totalPage; i++) {
			String url = site.getUrl() + "/?page=" + i;
			try {
				doc = loadPage(url);
				rowList = doc.select(ROW_LIST_PATH);
				browseJobList(rowList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, SiteMetaData site) {
		for (Element el : rowList) {
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
		Document doc = loadPage(job.getUrl());
		job.setSpec(doc.getElementById("job-details").text());
		Element jobE = doc.selectFirst("span[class=job-externalJobNo]");
		job.setReferenceId(jobE.text());
		jobE = doc.selectFirst("span[class=location]");
		job.setLocation(jobE.text());
		jobE = doc.selectFirst("span[class=categories]");
		job.setCategory(jobE.text());
		jobE = doc.selectFirst("a[class=apply-link button]");
		job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("span[class*=work-type]");
		job.setType(jobE.text());
		jobE = doc.selectFirst("span[class=open-date]");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text(), DF_1, DF_2));
		jobE = doc.selectFirst("span[class=close-date]");
		if (jobE != null) job.setDeadline(parseDate(jobE.text(), DF_1, DF_2));
		return job;
	}

	private int getTotalPage(Document doc) {
		Element el = doc.selectFirst("a[class=more-link button]");
		if (el == null) return 1;
		String totalJob = el.text().split("Jobs")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob) + JOB_PER_PAGE;
		return getPageCount(totalJob, JOB_PER_PAGE);
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