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
 * Willis Towers Watson Job site Parser<br>
 * URL: https://careers.willistowerswatson.com/job-search-results/
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @author fahim.reza
 * @since 2019-02-27
 */
@Service
@Slf4j
public class WillisTowers extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.WILLIS_TOWERS_WATSON;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver.get(site.getUrl());
		int totalPage = getTotalPage();
		for (int i = 1; i <= totalPage; i++) {
			String url = site.getUrl() + "?pg=" + i;
			try {
				browseJobList(url, site);				
			} catch (Exception e) {
				log.warn("Failed to parse job list of " + url, e);
			}
		}
		driver.quit();
	}

	private void browseJobList(String url, SiteMetaData site) throws InterruptedException {
		driver.get(url);
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='jobTitle']/a")));
		for (WebElement row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = row.getAttribute("href");
			try {
				saveJob(getJobDetail(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Document document = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Job job = new Job(url);
		Element jobE = document.getElementById("gtm-jobdetail-title");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = document.getElementById("gtm-jobdetail-date");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text().trim(), DF, DF1));
		jobE = document.getElementById("gtm-jobdetail-location");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = document.getElementsByClass("apply-btn").get(0);
		if (jobE != null) job.setApplicationUrl(jobE.attr("href").trim());
		jobE = document.getElementById("gtm-job-detail-desc");
		job.setSpec(jobE.text().trim());
		return job;
	}

	private int getTotalPage() {
		String totalJob = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("live-results-counter"))).getText();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 12);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
