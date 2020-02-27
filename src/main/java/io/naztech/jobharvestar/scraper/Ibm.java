package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

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
 * IBM Corporation jobs site parser <br>
 * URL: https://careers.ibm.com/ListJobs/All/Search/Country/US/
 * 
 * @author rahat.ahmad
 * @author tanmoy.tushar
 * @author fahim.reza
 * @since 2019-03-06
 */
@Slf4j
@Service
public class Ibm extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.IBM_CORPORATION;
	private String baseUrl;
	private static int count = 0;
	private static final int JOB_PER_PAGE = 30;
	private int expectedJobCount;
	private int maxRetry = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		String totalJobNumber = getTotalJob(siteMeta.getUrl());
		if (totalJobNumber == null) {
			throw new NullPointerException("Total page number not found");
		}
		expectedJobCount = Integer.parseInt(totalJobNumber);
		log.info("Total Job Found: " + getExpectedJob());
		int totalPage = getPageCount(totalJobNumber, JOB_PER_PAGE);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<Job> jobList = getSummaryPage(siteMeta.getUrl() + "Page-" + i);
			if (jobList.isEmpty())
				continue;
			for (Job job : jobList) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(job), siteMeta);
				}
				/**
				 * Sometime detail page got SocketTimeoutException. But If reload the page in
				 * browser, then it's working. To handle socketTimeoutException ,blindly reload
				 * this page 3 times.
				 */
				catch (SocketTimeoutException e) {
					maxRetry++;
					if (maxRetry < 3) {
						saveJob(getJobDetails(job), siteMeta);
					} else {
						log.warn("Failed to parse job detail of " + job.getUrl(), e);
					}
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job details of " + job.getUrl(), e);
				}
			}
		}
	}

	private List<Job> getSummaryPage(String url) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<Job>();
		try {
			Document doc = Jsoup.connect(url).get();
			Elements jobsE = doc.select("table.JobListTable > tbody > tr");
			for (int i = 2; i < jobsE.size(); i++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + jobsE.get(i).select("a").attr("href"));
				jobList.add(job);
			}
		} catch (Exception e) {
			log.warn("Failed to parse job list of " + url, e);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[id=job-description]");
		job.setSpec(jobE.text());

		jobE = doc.selectFirst("span[class=job-specs__header job-specs--employment]");
		if (jobE != null)
			job.setType(jobE.parent().text().split(":")[1].trim());
		jobE = doc.selectFirst("span[class=job-specs__header job-specs--category]");
		if (jobE != null)
			job.setCategory(jobE.parent().text().split(":")[1].trim());
		jobE = doc.selectFirst("span[class=job-specs__header job-specs--req-id]");
		if (jobE != null)
			job.setReferenceId(jobE.parent().text().split(":")[1].trim());
		jobE = doc.selectFirst("a[class=sf_applybtn]");
		if (jobE != null)
			job.setApplicationUrl(jobE.attr("href"));

		jobE = doc.selectFirst("span[class=job-specs__header job-specs--city]");
		if (jobE != null)
			job.setLocation(jobE.parent().text().split(":")[1].trim());
		jobE = doc.selectFirst("span[class=job-specs__header job-specs--state]");
		if (jobE != null)
			job.setLocation(job.getLocation() + ", " + jobE.parent().text().split(":")[1].trim());
		jobE = doc.selectFirst("span[class=job-specs__header job-specs--country]");
		if (jobE != null)
			job.setLocation(job.getLocation() + ", " + jobE.parent().text().split(":")[1].trim());

		return job;
	}

	private String getTotalJob(String url) throws IOException {
		Document docTotalJob = Jsoup.connect(url).get();
		Elements elTotalPage = docTotalJob.select("span.pager_counts");
		if (elTotalPage.isEmpty()) {
			if (count == 3)
				return null;
			count++;
			return getTotalJob(url);
		}
		String[] text = elTotalPage.get(0).text().split("of");
		return text[text.length - 1].trim();
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
