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
 * AMP LTD job site parser. <br>
 * URL: http://careers.amp.com.au/cw/en/listing
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-17
 */
@Slf4j
@Service
public class AmpLimited extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AMP_LTD;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, PageScrapingInterruptedException {
		this.baseUrl = site.getUrl().substring(0, 25);
		int totalPage = getTotalPage(site);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = site.getUrl() + "/?page=" + i;
			try {
				browseJobList(site, url);
			} catch (Exception e) {
				log.warn("Failed to parse job list of " + url, e);
			}
		}
	}

	private void browseJobList(SiteMetaData site, String url) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("tbody[id=search-results-content]>tr>td>a");
		for (Element element : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = getBaseUrl() + element.attr("href");
			try {
				saveJob(getJobDetails(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + jobUrl, e);
			}
		}
	}

	private Job getJobDetails(String url) throws IOException {
		Document doc = Jsoup.connect(url).timeout(TIME_5S).get();
		Job job = new Job(url);
		Element jobE = doc.selectFirst("h1");
		if (jobE == null) jobE = doc.selectFirst("h2");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[class=job-externalJobNo]");
		if (jobE != null) {
			job.setReferenceId(jobE.text());
			String type = jobE.nextElementSibling().nextElementSibling().nextElementSibling().text();
			if (type.contains("Time")) job.setType(type);
		}
		jobE = doc.selectFirst("span[class=location]");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("span[class=categories]");
		if (jobE != null) job.setCategory(jobE.text());
		jobE = doc.selectFirst("span[class=open-date]>time");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text().substring(0, 11), DF));
		jobE = doc.selectFirst("span[class=close-date]>time");
		if (jobE != null) job.setDeadline(parseDate(jobE.text().substring(0, 11), DF));
		jobE = doc.selectFirst("a[class=apply-link button]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.getElementById("job-details");
		job.setSpec(jobE.text());
		return job;
	}

	private int getTotalPage(SiteMetaData site) throws IOException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		Element el = doc.selectFirst("a[class=more-link button]>span");
		if (el == null)	return 1;
		String totalJob = el.text().trim();
		expectedJobCount = Integer.parseInt(totalJob) + 20;
		return getPageCount(totalJob, 20) + 1;
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
