package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ElementNotFoundException;
import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Apple job site parsing class. <br>
 * URL: https://jobs.apple.com/en-us/search?page=1
 * 
 * @author tanmoy.tushar
 * @since 2019-04-28
 */
@Service
@Slf4j
public class Apple extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.APPLE;
	private static final DateTimeFormatter DF_1 = DateTimeFormatter.ofPattern("MMM dd, yyy");
	private static final DateTimeFormatter DF_2 = DateTimeFormatter.ofPattern("MMM d, yyy");
	private static final String ADD_URL = "/en-us/search?page=";
	private String baseUrl;
	private int expectedJobCount;
	private int maxRetry = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 22);
		Document doc = Jsoup.connect(site.getUrl()).timeout(TIME_1M).get();
		int totalP = getTotalPage(doc);
		expectedJobCount = totalP * 20;
		for (int i = 1; i <= totalP; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			browseJobList(getBaseUrl() + ADD_URL + i, site);
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws PageScrapingInterruptedException {
		try {
			Document doc = Jsoup.connect(url).timeout(TIME_1M).get();
			Elements rowList = doc.select("table[id=tblResultSet]>tbody>tr>td>a");
			for (Element row : rowList) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				String jobUrl = getBaseUrl() + row.attr("href");
				try {
					saveJob(getJobDetail(jobUrl), site);
					maxRetry=0;
				}
				/**
				 * Sometime detail page got SocketTimeoutException. But If reload the page in
				 * browser, then it's working. To handle socketTimeoutException ,blindly reload
				 * this page 3 times.
				 */
				catch (SocketTimeoutException e) {
					maxRetry++;
					if (maxRetry < 3) {
						saveJob(getJobDetail(jobUrl), site);
					} else {
						log.warn("Failed to parse job detail of " + jobUrl, e);
					}
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + url, e);
				}
			}
		} catch (IOException | ElementNotFoundException e) {
			log.warn("Failed to parse job list of " + url, e);
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Document doc = Jsoup.connect(url).timeout(TIME_1M).get();
		Job job = new Job(url);

		Element jobE = doc.selectFirst("h1");
		if (jobE == null)
			throw new ElementNotFoundException("Job title not found; " + url);
		job.setTitle(jobE.text());
		job.setName(job.getTitle());

		jobE = doc.selectFirst("div[id=jd-description]");
		if (jobE != null)
			job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("div[id=job-location-name]");
		if (jobE != null)
			job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("strong[id=jobPostDate]");
		if (jobE != null)
			job.setPostedDate(parseDate(jobE.text().trim(), DF_1, DF_2));
		jobE = doc.selectFirst("strong[id=jobNumber]");
		if (jobE != null)
			job.setReferenceId(jobE.text().trim());
		jobE = doc.selectFirst("div[id=jd-key-qualifications]");
		if (jobE != null)
			job.setPrerequisite(jobE.text().trim());
		jobE = doc.selectFirst("div[id=jd-additional-requirements]");
		if (jobE != null)
			job.setPrerequisite(job.getPrerequisite() + " " + jobE.text().trim());
		jobE = doc.selectFirst("div[id=jd-education-experience]");
		if (jobE != null)
			job.setPrerequisite(job.getPrerequisite() + " " + jobE.text().trim());
		jobE = doc.selectFirst("a[class=btn btn--md btn--blue-gradient]");
		if (jobE != null)
			job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		return job;
	}

	private int getTotalPage(Document doc) {
		Element totalPage = doc.select("span[class=pageNumber]").get(1);
		return Integer.parseInt(totalPage.text().trim());
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
