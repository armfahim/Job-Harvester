package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
 * Byju's Job Site Parser <br>
 * URL: https://byjus.com/careers-at-byjus/
 * 
 * @author Rahat Ahmad
 * @since 2019-03-11
 */
@Slf4j
@Service
public class Byju extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.BYJUS;
	private String baseUrl;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 20);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<String> jobPageUrl = getSummaryPage(siteMeta);
		for (String url : jobPageUrl) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<Job> jobList = getAllJob(url);
			if (jobList.isEmpty()) continue;
			expectedJobCount += jobList.size();
			for (Job job : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
					exception = e;
				}
			}
		}
		driver.quit();
	}

	private List<String> getSummaryPage(SiteMetaData siteMeta) throws PageScrapingInterruptedException {
		List<String> jobLink = new ArrayList<>();
		try {
			driver.get(siteMeta.getUrl());
			List<WebElement> jobLinks = driver.findElements(By.xpath("//div[@class='col-sm-12 col-xs-12 col-md-4']/a"));
			for (WebElement webElement : jobLinks) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				jobLink.add(webElement.getAttribute("href"));
			}
		} catch (Exception e) {
			log.warn("Failed to collect category url: " + siteMeta.getUrl(), e);
			throw e;
		}
		return jobLink;
	}

	private List<Job> getAllJob(String url) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		try {
			driver.get(url);
			List<WebElement> el = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//ul[@class='job_listings job-list full']/li/a")));
			for (WebElement element : el) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					Job job = new Job(element.getAttribute("href"));
					job.setTitle(element.findElement(By.tagName("h4")).getText());
					job.setName(job.getTitle());
					jobList.add(job);
				} catch (NoSuchElementException | TimeoutException e) {
					log.warn("No element found for url : " + url, e);
				}
			}
		} catch (TimeoutException e) {
			log.warn("No job found for the category url : " + url, e);
			throw e;
		}
		return jobList;
	}

	private Job getJobDetails(Job job) {
		driver.get(job.getUrl());
		job.setSpec(wait.until(ExpectedConditions.presenceOfElementLocated(By.className("job_description"))).getText());
		try {
			WebElement jobE = driver.findElement(By.xpath("//div[@class='eleven columns']/span"));
			job.setCategory(jobE.getText());
			jobE = driver.findElement(By.className("location"));
			job.setLocation(jobE.getText());
			jobE = driver.findElement(By.xpath("//div[@class='job-overview']/a"));
			job.setApplicationUrl(jobE.getAttribute("href"));
		} catch (NoSuchElementException e) {
			log.info("Application Url/Location/Category url not found for : " + job.getUrl());
		}
		return job;
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
