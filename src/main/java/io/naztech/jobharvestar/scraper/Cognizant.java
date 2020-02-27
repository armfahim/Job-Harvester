package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
 * Cognizant job site parser. <br>
 * URL: https://careers.cognizant.com/global/en/search-results?
 * NB: This site is possible with HtmlUnit but it took too much time to parse job. That's why site move to Selenium Web Driver
 * 
 * @author tanmoy.tushar
 * @since 2019-10-21
 */
@Slf4j
@Service
public class Cognizant extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.COGNIZANT;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String ROW_LIST = "//a[@ph-tevent='job_click']";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		driver.get(site.getUrl());
		this.baseUrl = site.getUrl() + "from=";
		int totalJob = getTotalJob();
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
		List<Job> jobList = new ArrayList<>();
		jobList.addAll(browseJobList(rowList, site));
		for (int i = 50; i < totalJob; i += 50) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + i + "&s=1";
			try {
				driver.get(url);
				rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
				jobList.addAll(browseJobList(rowList, site));
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
		log.info("Total Job Found: " + jobList.size());
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);				
			}
		}
	}

	private List<Job> browseJobList(List<WebElement> rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		for (WebElement el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			job.setTitle(el.getAttribute("data-ph-at-job-title-text"));
			job.setName(job.getTitle());
			job.setLocation(el.getAttribute("data-ph-at-job-location-text"));
			job.setCategory(el.getAttribute("data-ph-at-job-category-text"));
			job.setReferenceId(el.getAttribute("data-ph-at-job-id-text"));
			job.setPostedDate(parseDate(el.getAttribute("data-ph-at-job-post-date-text").substring(0, 10), DF));
			jobList.add(job);			
		}
		return jobList;
	}

	private Job getJobDetail(Job job) throws IOException {
		driver.get(job.getUrl());
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//section[@class='job-description']")));
		job.setSpec(el.getText().trim());
		return job;
	}

	private int getTotalJob() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='result-count']")));
		String totalJob = el.getText().trim();
		return expectedJobCount = Integer.parseInt(totalJob);
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
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
