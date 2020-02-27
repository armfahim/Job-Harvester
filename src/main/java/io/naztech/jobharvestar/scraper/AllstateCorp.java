package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Allstate Corp job site parsing class. <br>
 * URL: https://www.allstate.jobs/job-search-results/
 * 
 * @author tanmoy.tushar
 * @since 2019-02-24
 */
@Service
@Slf4j
public class AllstateCorp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ALLSTATE_CORP;
	private static final String JOB_ROW_LIST_PATH = "//div[@class='jobTitle']/a";
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	private ChromeDriver driver;
	private WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 40);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 25);
		driver.get(site.getUrl());
		int totalPage = getTotalPages();
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = site.getUrl() + "?pg=" + i;
			try {
				browseJobList(site, url);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(SiteMetaData site, String url) throws PageScrapingInterruptedException {
		driver.get(url);
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_ROW_LIST_PATH)));
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = jobList.get(i).getAttribute("href");
			try {
				saveJob(getJobDetails(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + jobUrl, e);
			}
		}
	}

	private Job getJobDetails(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.getElementById("gtm-jobdetail-title").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.getElementById("gtm-jobdetail-desc").text().trim());
		Element jobE = doc.getElementById("gtm-jobdetail-category");
		if (jobE != null) job.setCategory(jobE.text().trim());
		jobE = doc.getElementById("gtm-jobdetail-date");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text().trim(), DF1, DF2));
		jobE = doc.selectFirst("a[class=button apply-btn]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		job.setLocation(getJobLocation(doc));
		return job;
	}

	private String getJobLocation(Document doc) {
		String loc = "";
		Element jobE = doc.getElementById("gtm-jobdetail-city");
		if (jobE != null) loc = jobE.text().trim();
		jobE = doc.getElementById("gtm-jobdetail-state");
		if (jobE != null) loc +=  ", " + jobE.text().trim();
		jobE = doc.getElementById("jobdetail-country");
		if (jobE != null) loc +=  ", " + jobE.text().trim();
		return loc.trim();
	}

	private int getTotalPages() {
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("live-results-counter")));
		String totalJob = driver.findElementById("live-results-counter").getText();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 12);
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
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}