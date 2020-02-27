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
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * BrewDog job site parser<br>
 * URL: https://www.jobs.brewdog.com
 * 
 * @author BM Al-Amin
 * @author iftekar.alam
 * @since 2019-03-13
 */
@Service
@Slf4j
public class BrewDog extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BREWDOG;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
		driver.get(siteMeta.getUrl());
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy
				(By.xpath("//a[@class='jobListItemContainer sc-bZQynM bogiQ']")));
		expectedJobCount=rowList.size();
		for (Job job : browseJobList(rowList)) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+job.getUrl(),e);
			}
		}
	}
	
	private List<Job> browseJobList(List<WebElement> rowList) throws PageScrapingInterruptedException{
		List<Job> jobList = new ArrayList<>();
		for (WebElement el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(el.getAttribute("href"));
			job.setTitle(el.findElement(By.tagName("div")).findElement(By.tagName("div")).getText().trim());
			job.setName(job.getTitle());
			job.setLocation(el.findElement(By.tagName("div")).findElement(By.tagName("span")).getText().split(":")[1].trim());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetail(Job job) {
		driver.get(job.getUrl());
		WebElement spec = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='jobContent']")));
		job.setSpec(spec.getText().trim());
		return job;
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