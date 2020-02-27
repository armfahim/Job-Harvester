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
 * York Capital Management Job site Parser<br>
 * URL: https://www.careerbuilder.com/jobs-york-capital-management
 * 
 * @author fahim.Reza
 * @author jannatul.maowa
 * @author tanmoy.tushar
 * @since 2019-03-07
 */
@Service
@Slf4j
public class YorkCapitalManagement extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.YORK_CAPITAL_MANAGEMENT;
	private static final String TAILURL = "/jobs?keywords=york-capital-management&page_number=";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 29);
		int totalPage = getTotalPage(site);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + TAILURL + i;
			try {
				browseJobList(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document document = Jsoup.connect(url).get();
		Elements jobRow = document.select("a[class=data-results-content block job-listing-item]");
		for (Element element : jobRow) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = getBaseUrl() + element.attr("href");
			Job job = new Job(jobUrl);
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + jobUrl, e);
			}
		}
	}

	public Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).timeout(TIME_10S).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		Elements jobInfo = doc.select("div[class=data-display-header_info-content dib-m]>div>span");
		if (jobInfo.size() == 3) {
			job.setLocation(jobInfo.get(1).text());
			job.setType(jobInfo.get(2).text());
		}
		jobE = doc.selectFirst("a[class=btn btn-linear btn-linear-green btn-block]");
		if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		jobE = doc.getElementById("jdp_description");
		job.setSpec(jobE.text());
		return job;
	}

	private int getTotalPage(SiteMetaData site) throws IOException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		String totalJob = doc.selectFirst("div[id=jobs-found]>div>div").text().trim().split(" ")[0].trim();
		if(totalJob.contains(",")) {
			totalJob = totalJob.replace(",", "");
			expectedJobCount = Integer.parseInt(totalJob);
		}
		else 
			expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 25);
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