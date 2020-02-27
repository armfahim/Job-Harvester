package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Fujitsu job site parser. <br>
 * URL: https://fujitsu.referrals.selectminds.com/jobs/search
 * 
 * @author tanmoy.tushar
 * @since 2019-10-17
 */
@Slf4j
@Service
public class Fujitsu extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.FUJITSU_SERVICES;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final String ROW_LIST = "//a[@class='job_link font_bold']";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private WebClient client;

	@Override
	public void scrapJobs() throws Exception {
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(site.getUrl());
		int totalPage = getTotalPage(page);
		List<HtmlElement> jobList = page.getByXPath(ROW_LIST);
		browseJobList(jobList, site);
		HtmlElement getPageLink = page.getFirstByXPath("//a[@class='link_title font_bold jSearchLink']");
		this.baseUrl = getPageLink.getAttribute("href") + "/page";
		for (int i = 2; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + i;
			try {
				page = client.getPage(url);
				jobList = page.getByXPath(ROW_LIST);
				browseJobList(jobList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list no - " + url, e);
			}
		}
	}

	private void browseJobList(List<HtmlElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (HtmlElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = el.getAttribute("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=job_description]").text().trim());
		Element jobE = doc.selectFirst("h4");
		if (jobE != null) job.setLocation(jobE.text().substring(2).trim());
		jobE = doc.selectFirst("dd[class=job_external_id]>span");
		if (jobE != null) job.setReferenceId(jobE.text().trim());
		jobE = doc.selectFirst("dd[class=job_post_date]>span");
		if (jobE != null) {
			job.setPostedDate(parseAgoDates(jobE.text().trim()));
			if (job.getPostedDate() == null) job.setPostedDate(parseDate(jobE.text(), DF));
		}
		jobE = doc.selectFirst("dl[class=field_category]>dd>a>span");
		if (jobE != null) job.setCategory(jobE.text().trim());
		return job;
	}

	private int getTotalPage(HtmlPage page) {
		HtmlElement el = page.getFirstByXPath("//span[@class='total_results']");
		String totalJob = el.asText().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
