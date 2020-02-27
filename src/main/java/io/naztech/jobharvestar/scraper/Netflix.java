package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * NetFlix job site parser.<br>
 * URL: https://jobs.netflix.com/search
 * 
 * @author Rahat Ahmad
 * @since 2019-03-06
 */
@Service
@Slf4j
public class Netflix extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NETFLIX;
	private String baseUrl;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		int totalJob = Integer.parseInt(getTotalJob(siteMeta.getUrl()));
		int totalPage = totalJob / 20;
		expectedJobCount = totalJob;
		for (int i = 2; i <= totalPage + 1; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<Job> jobList = getSummaryPage();
			for (Job job : jobList) {
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
			if (i > totalPage) break;
			driver.get(siteMeta.getUrl() + "?page=" + i);
		}
		driver.quit();
	}

	private List<Job> getSummaryPage() throws PageScrapingInterruptedException {
		List<WebElement> jobsE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='css-17670uj exb5qdx0']/section")));
		List<Job> jobList = new ArrayList<Job>();
		for (int i = 0; i < jobsE.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setUrl(jobsE.get(i).findElement(By.tagName("a")).getAttribute("href"));
			job.setTitle(jobsE.get(i).findElement(By.tagName("a")).getText());
			job.setName(job.getTitle());
			job.setLocation(jobsE.get(i).findElements(By.tagName("span")).get(0).getText());
			job.setCategory(jobsE.get(i).findElements(By.tagName("span")).get(1).getText());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) {
		try {
			driver.get(job.getUrl());
			WebElement jobsDetailE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='css-1mixv7j e1spn5rx0']")));
			job.setSpec(jobsDetailE.getText());
			return job;
		} catch (TimeoutException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
			return null;
		}		
	}

	private String getTotalJob(String url) {
		driver.get(url);
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("css-2t20s4")));
		return el.getText().replace("of ", "").trim();
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
