package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
 * Deutsche Bank job site parsing class. <br>
 * URL: https://www.db.com/careers/en/prof/role-search/job_search_results.html#
 * 
 * @author tanmoy.tushar
 * @since 2019-04-02
 */
@Service
@Slf4j
public class DeutscheBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DEUTSCHE_BANK;
	private static final String JOB_LIST_PATH = "//a[@class='container singleresult findOutMore']";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		driver.get(site.getUrl());
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_LIST_PATH)));
		getSummaryPages(site, jobList);
		driver.quit();
	}

	private void getSummaryPages(SiteMetaData site, List<WebElement> jobList) throws InterruptedException {
		int totalJob = getTotalJob();
		expectedJobCount = totalJob;
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setTitle(jobList.get(i).findElement(By.tagName("h4")).getText());
			job.setName(job.getTitle());
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", jobList.get(i));
			WebElement jobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='db-jobad']")));
			job.setUrl(driver.getCurrentUrl());
			try {
				saveJob(getJobDetails(job, jobE), site);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
			WebElement backBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='custom-button arrow-left-btn backtoresults']")));
			js.executeScript("arguments[0].click();", backBtn);
			Thread.sleep(TIME_4S);
			if (i == totalJob - 1) break;
			if (i == jobList.size() - 1) {
				WebElement moreJobsE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='custom-button action-btn showmore hr-type-a']")));
				js.executeScript("arguments[0].click();", moreJobsE);
				Thread.sleep(TIME_5S);
			}
			jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_LIST_PATH)));
		}
	}

	private Job getJobDetails(Job job, WebElement jobE) throws PageScrapingInterruptedException {
		List<WebElement> jobInfoL = jobE.findElements(By.tagName("td"));
		if (jobInfoL.size() > 4) {
			job.setReferenceId(jobInfoL.get(0).getText().split(":")[1].trim());
			try {
				job.setType(jobInfoL.get(1).getText().split(":")[1].trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// Intentionally left Blank
			}
			String[] dateParts = jobInfoL.get(3).getText().split(":");
			if (dateParts.length == 2)
				job.setPostedDate(parseDate(dateParts[1].trim(), DF));
			String[] locParts = jobInfoL.get(4).getText().split(":");
			if (locParts.length == 2)
				job.setLocation(locParts[1].trim());
		}
		List<WebElement> jobDetailsL = jobE.findElements(By.tagName("ul"));
		if (jobDetailsL.size() > 1) {
			job.setSpec(jobDetailsL.get(0).getText());
			job.setPrerequisite(jobDetailsL.get(1).getText());
		} else {
			job.setSpec(jobE.getText());
		}
		return job;
	}

	private int getTotalJob() {
		WebElement totalJob = wait.until(presenceOfElementLocated(By.xpath("//span[@class='totalamount']")));
		return Integer.parseInt(totalJob.getText());
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