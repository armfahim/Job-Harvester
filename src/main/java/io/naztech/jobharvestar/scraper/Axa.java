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
 * AXA job site scrapper<br>
 * URL: https://careers.jobs.axa/jobs/search
 * 
 * @author fahim.reza
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-02-10
 */
@Service
@Slf4j
public class Axa extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AXA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		int totalPage = getTotalPage(site.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/page" + i;
			try {
				browseJobList(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.select("div.jlr_title");
		for (Element element : els) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(element.child(1).child(0).attr("href"));
			job.setTitle(element.child(1).child(0).text());
			job.setName(job.getTitle());
			job.setLocation(element.child(3).child(1).text().trim());
			job.setCategory(element.child(4).child(1).text());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element spec = doc.selectFirst("div.job_description");
		job.setSpec(spec.text());
		Element postedDate = doc.selectFirst("dd.job_post_date").getElementsByTag("span").get(0);
		if (postedDate.text().contains("ago")) {
			job.setPostedDate(parseAgoDates(postedDate.text()));
		} else {
			job.setPostedDate(parseDate(postedDate.text(), DF));
		}

		return job;
	}

	private int getTotalPage(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		this.baseUrl = doc.selectFirst("div[class=list_row_title]>a").attr("href");
		Element el = doc.select("span.total_results").get(0);
		String totalJob = el.text();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
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