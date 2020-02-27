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
 * Sap job site parser <br>
 * URL: https://jobs.sap.com/search/?q=&locationsearch=&locale=en_US
 * 
 * @author muhammad.tarek
 * @author iftekar.alam
 * @author fahim.reza
 * @since 2019-03-06
 */
@Slf4j
@Service
public class Sap extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SAP;
	private String baseUrl;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 20);
		int totalPage = getTotalJob(site.getUrl());
		for (int i = 1; i < totalPage; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			getSummaryPage(
					getBaseUrl() + "/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=" + (i * 25 - 25),
					site);
		}
	}

	private void getSummaryPage(String url, SiteMetaData site) throws InterruptedException {
		try {
			Document doc = Jsoup.connect(url).get();
			Elements jobList = doc.select("tr[class=data-row clickable]");
			for (Element tr : jobList) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + tr.getElementsByTag("a").get(0).attr("href"));
				job.setTitle(tr.getElementsByTag("a").get(0).text().trim());
				job.setName(job.getTitle());
				job.setLocation(tr.getElementsByTag("td").get(1).text().trim());
				try {
					saveJob(getJobDetail(job), site);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse details of " + job.getUrl(), e);
				}
			}
		} catch (IOException e) {
			log.warn("Failed to load page " + url, e);
		}

	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element applicationUrl = doc.selectFirst("button[class=btn btn-primary btn-large btn-lg dropdown-toggle]");
		job.setApplicationUrl(applicationUrl.attr("href"));
		Element postedDate = doc.selectFirst("p[id=job-date]");
		job.setPostedDate(parseDate(postedDate.text().split(":")[1].trim(), DF, DF1));
		Element spec = doc.selectFirst("div[class=job]");
		job.setSpec(spec.text());
		try {
			Element el = doc.selectFirst("span[class=jobdescription]>p>span>span");
			if (el != null) {
				String[] parts = el.text().trim().split(":");
				if (parts[0].contains("Requisition ID"))
					job.setReferenceId(parts[1].trim().split(" ")[0].trim());
				if (parts[parts.length - 2].contains("Employment Type"))
					job.setType(parts[parts.length - 1].trim());
			}
		}
		/**
		 * In some detail page, there is no reference ID and Type
		 * To handle this exception as well as save that job.
		 */
		catch (IndexOutOfBoundsException e) {
			return job;
		}
		return job;
	}

	private int getTotalJob(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Element totalJob = doc.select("span[class=paginationLabel]>b").last();
		expectedJobCount = Integer.parseInt(totalJob.text());
		return getPageCount(totalJob.text(), 25);
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
