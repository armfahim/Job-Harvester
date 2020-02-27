package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Jefferies Financial Group. <br>
 * URL: https://jefferies.taleo.net/careersection/jef_ex/jobsearch.ftl 
 * 
 * @author naym.hossain
 * @since 2019-02-18
 */
@Service
@Slf4j
public class Jefferies extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.JEFFERIES_FINANCIAL_GROUP;
	private String baseUrl;
	private static final String JOB_POST = "requisitionDescriptionInterface.reqTitleLinkAction.row1";
	private static final String JOB_ID = "requisitionDescriptionInterface.reqContestNumberValue.row1";
	private static final String JOB_LOCATION = "requisitionDescriptionInterface.ID1676.row1";
	private static final String JOB_TYPE = "requisitionDescriptionInterface.ID1988.row1";
	private static final String JOB_CATEGORY = "requisitionDescriptionInterface.ID1888.row1";
	private static final String JOB_POSTED_DATE = "requisitionDescriptionInterface.reqPostingDate.row1";
	private static final String JOB_DES = "requisitionDescriptionInterface.ID1517.row1";
	private static final String JOB_PREREQ = "requisitionDescriptionInterface.ID1575.row1";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d yyyy");
	
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		List<String> jobLinks = new ArrayList<>();
		driver.get(site.getUrl());
		while (true) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			jobLinks.addAll(getSummaryPages());
			if (!hasNextPage()) break;
			List<WebElement> nextPages = wait.until(numberOfElementsToBeMoreThan(By.xpath("//a[@id = 'next']"), 0));
			nextPages.get(0).click();
			Thread.sleep(RandomUtils.nextInt(4000, 5000));
		}
		expectedJobCount = jobLinks.size();
		for (String link : jobLinks) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(link);
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
		driver.quit();
	}

	private List<String> getSummaryPages() throws InterruptedException {
		wait = new WebDriverWait(driver, 30);
		List<WebElement> jobList = wait.until(numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"jobs\"]/tbody/tr/th/div/div/span/a"), 0));
		List<String> jobLinks = new ArrayList<>();
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = jobList.get(i).getAttribute("href");
			jobLinks.add(jobUrl);
		}
		return jobLinks;
	}

	private Job getJobDetails(Job job) throws InterruptedException {
		try {
			driver.get(job.getUrl());
			Thread.sleep(RandomUtils.nextInt(4000, 5000));
			wait = new WebDriverWait(driver, 60);
			List<WebElement> jobTitle = wait.until(presenceOfAllElementsLocatedBy(By.id(JOB_POST)));
			job.setTitle(jobTitle.get(0).getText());
			job.setName(job.getTitle());
			List<WebElement> jobId = wait.until(presenceOfAllElementsLocatedBy(By.id(JOB_ID)));
			job.setReferenceId(jobId.get(0).getText());
			List<WebElement> jobLoc = wait.until(presenceOfAllElementsLocatedBy(By.id(JOB_LOCATION)));
			job.setLocation(jobLoc.get(0).getText());
			List<WebElement> jobCat = wait.until(presenceOfAllElementsLocatedBy(By.id(JOB_CATEGORY)));
			job.setCategory(jobCat.get(0).getText());
			List<WebElement> jobType = wait.until(presenceOfAllElementsLocatedBy(By.id(JOB_TYPE)));
			job.setType(jobType.get(0).getText());
			List<WebElement> jobDes = wait.until(presenceOfAllElementsLocatedBy(By.id(JOB_DES)));
			job.setSpec(jobDes.get(0).getText().trim());
			List<WebElement> jobPre = wait.until(presenceOfAllElementsLocatedBy(By.id(JOB_PREREQ)));
			job.setPrerequisite(jobPre.get(0).getText().trim());
			List<WebElement> postedDate = wait.until(presenceOfAllElementsLocatedBy(By.id(JOB_POSTED_DATE)));
			String[] date = postedDate.get(0).getText().split(",");
			String jobDate = date[0] + date[1];
			job.setPostedDate(parseDate(jobDate.trim(), DF));
			if (job.getPostedDate() == null) log.info(" failed to parse date value " + jobDate.trim() + " for job " + job.getUrl());
			return job;
		} catch (TimeoutException e) {
			log.warn("Failed to parse job details of " + job.getUrl());
			return null;
		}
	}

	private boolean hasNextPage() {
		List<WebElement> nextPages = wait.until(numberOfElementsToBeMoreThan(By.xpath("//a[@id = 'next']"), 0));
		return !nextPages.get(0).getAttribute("aria-disabled").equals("true");
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
