package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
 * ComcastCorporation Job Site Parser<br>
 * URL: https://comcast.jibeapply.com/jobs?page=1
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-03-10
 */
@Slf4j
@Service
public class ComcastCorporation extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.COMCAST_CORPORATION;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final int JOB_PER_PAGE = 10;
	private String baseUrl;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 40);
		driver = getChromeDriver(false);
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		driver.get(siteMeta.getUrl());
		wait = new WebDriverWait(driver, 60);
		int totalPage = getTotalPage();
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				if (i != 1) driver.get(getBaseUrl() + i);
				getSaveJob(getSummaryPage(), siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + driver.getCurrentUrl(), e);
			}			
		}
		driver.quit();
	}
	
	private List<Job> getSummaryPage() throws PageScrapingInterruptedException {
		List<WebElement> jobListE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("job-title-link")));
		List<Job> jobList = new ArrayList<>();
		for (WebElement el : jobListE) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			job.setTitle(el.getText().trim());
			job.setName(job.getTitle());
			jobList.add(job);
		}
		return jobList;
	}
	
	private void getSaveJob(List<Job> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) {
		driver.get(job.getUrl());
		wait = new WebDriverWait(driver, 10);
		try {
			job.setSpec(wait.until(ExpectedConditions.presenceOfElementLocated(By.className("job-detail-description-api"))).getText().trim());
		} catch (TimeoutException e) {
			driver.findElementByClassName("job-detail-loading-status");
			getJobDetails(job);
		}
		job.setCategory(driver.findElementByClassName("job-detail-api-industrycode").getText().trim());
		job.setLocation(driver.findElementByClassName("job-detail-api-all_locations").getText().trim());
		job.setReferenceId(driver.findElementByClassName("job-detail-api-postingid").getText().trim());
		job.setType(driver.findElementByClassName("job-detail-api-job_status").getText().trim());
		if (getPostedDate() != null) job.setPostedDate(parseDate(getPostedDate(), DF));
		job.setApplicationUrl(driver.findElementByClassName("job-detail-api-apply_url").getAttribute("href"));
		return job;
	}
	
	private int getTotalPage() {
		String totalJob = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search-results-indicator"))).getText().replace("Results", "").trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOB_PER_PAGE);
	}
	
	private String getPostedDate() {
		try {
			String[] parts = driver.findElementsByClassName("job-detail-api-pub_date").get(1).getText().split("/");
			return parts[0].trim() + "/" + parts[1].trim() + "/20" + parts[2].trim();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
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
