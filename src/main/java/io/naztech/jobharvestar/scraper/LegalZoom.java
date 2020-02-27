package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
 * LigalZoom job site parsing class. <br>
 * URL: https://www.legalzoom.com/careers/all-positions?ccc=Search%20All%20Jobs
 * 
 * @author rahat.ahmad
 * @since 2019-03-13
 */
@Slf4j
@Service
public class LegalZoom extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.LEGALZOOM;
	private static final String TOTAL_JOB = "//table[@class='jv-job-list']/tbody/tr/td/a";
	private String baseUrl;
	private static WebDriverWait wait;
	private static ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl());
		Thread.sleep(TIME_10S*2);
		driver.switchTo().frame(0);
		List<Job> jobList = getSummarypage();
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
		driver.quit();
	}
	
	private List<Job> getSummarypage() throws PageScrapingInterruptedException{
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(TOTAL_JOB)));
		List<Job> jobList = new ArrayList<>();
		for (WebElement webElement : jobLinks) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setUrl(webElement.getAttribute("href"));
			job.setTitle(webElement.getText());
			job.setName(job.getTitle());
			jobList.add(job);
		}
		return jobList;
	}
	
	private Job getJobDetail(Job job) throws InterruptedException {
		driver.get(job.getUrl());
		Thread.sleep(TIME_10S*2);
		driver.switchTo().frame(0);
		try {
			job.setSpec(driver.findElement(By.xpath("//div[@class='jv-wrapper']/div[2]")).getText());
			job.setApplicationUrl(driver.findElement(By.xpath("//div[@class='jv-job-detail-top-actions']/a")).getAttribute("href"));
		}catch(NoSuchElementException e) {
			log.warn(" failed to parse detail page of" + job.getUrl(), e);
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
