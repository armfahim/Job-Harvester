package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Abstract of BambooHR site https://xapo.bamboohr.com/jobs/
 * https://cloudmargin.bamboohr.com/jobs/
 * 
 * @author sohid.ullah
 * @since 2019-04-15
 */
public abstract class AbstractBambooHr extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled())
			log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver.get(site.getUrl());
		List<Job> jobList = new ArrayList<>();
		List<WebElement> rowList = wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='ResAts__listing-link']")));
		jobList.addAll(browseJobList(site, rowList));
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), site);				
			} catch(Exception e) {
				exception = e;
			}
		}
		driver.quit();
	}

	private List<Job> browseJobList(SiteMetaData site, List<WebElement> jobE) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		int numberOfJob = jobE.size();
		for (int i = 0; i < numberOfJob; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(jobE.get(i).getAttribute("href"));
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) {
		driver.get(job.getUrl());
		try {
			WebElement jobE = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("//div[@class='col-xs-12 col-sm-8 col-md-12']")));
			job.setTitle(jobE.findElement(By.tagName("h2")).getText());
			job.setName(job.getName());
			String categoryAndLocation = jobE.findElement(By.tagName("span")).getText();
			String categoryLocationArr[] = categoryAndLocation.split("–");
			job.setCategory(categoryLocationArr[0].trim());
			job.setLocation(categoryLocationArr[1].trim());
			WebElement jobSpecE = wait.until(ExpectedConditions.presenceOfElementLocated(
					By.xpath("//div[@class='ResAts__page ResAts__description js-jobs-page js-jobs-description']")));
			job.setSpec(jobSpecE.getText());
		} catch (ElementNotFoundException e) {
			log.warn("Failed to parse job Details",job.getUrl(), e);
		}
		return job;
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
