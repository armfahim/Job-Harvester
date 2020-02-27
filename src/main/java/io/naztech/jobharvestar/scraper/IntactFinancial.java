package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
 * Intact Financial jobs site parser<br>
 * URL: https://careers.intact.ca/ca/en/search-results
 * 
 * @author naym.hossain
 * @since 2019-02-27
 */
@Slf4j
@Service
public class IntactFinancial extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INTACT_FINANCIAL;
	private static final String HEADURL = "/ca/en/search-results?from=";
	private static final String TAILURL = "&s=1";
	private static final int JOBPERPAGE = 50;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d['st']['nd']['rd']['th'] yyyy");
	private String baseUrl;
	private WebDriverWait wait;	
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(70, TimeUnit.SECONDS);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		try {
			this.baseUrl = site.getUrl().substring(0, 25);
			int totalPage = getTotalPage();
			for (int i = 1; i <= totalPage; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				getsummaryPages(getBaseUrl() + HEADURL + (i * JOBPERPAGE - JOBPERPAGE) + TAILURL, site);
			}
		}catch (TimeoutException e) {
			log.warn("Exception on Job Summary Page" + e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private int getTotalPage() {
		driver.get(getBaseUrl() + HEADURL + "0" + TAILURL);
		wait = new WebDriverWait(driver, 30);
		WebElement eltotalJob = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class = 'result-count']")));
		expectedJobCount = Integer.parseInt(eltotalJob.getText().trim());
		return getPageCount(eltotalJob.getText().trim(), JOBPERPAGE);
	}

	private void getsummaryPages(String url, SiteMetaData site) throws InterruptedException {
		driver.get(url);
		wait = new WebDriverWait(driver, 30);
		List<WebElement> jobList = wait
				.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//li[@class = 'jobs-list-item']"), 0));
		for (int j = 0; j < jobList.size(); j++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = jobList.get(j).findElements(By.tagName("div")).get(0).findElement(By.tagName("a"))
					.getAttribute("href");
			Job job = new Job(jobUrl);
			WebElement atag = jobList.get(j).findElements(By.tagName("div")).get(0).findElement(By.tagName("a"));
			job.setTitle(atag.findElement(By.tagName("h4")).getText());
			job.setName(atag.findElement(By.tagName("h4")).getText());
			List<WebElement> jobInfos = atag.findElement(By.tagName("div")).findElements(By.tagName("span"));
			for (int i = 0; i < jobInfos.size();) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				if (jobInfos.get(i).getAttribute("class").equals("job-type")) {
					job.setType(jobInfos.get(i).getText());
					i++;
				} else if (jobInfos.get(i).getAttribute("class").equals("job-location")) {
					job.setLocation(jobInfos.get(i).getText());
					i++;
				} else if (jobInfos.get(i).getAttribute("class").equals("job-category")) {
					job.setCategory(jobInfos.get(i).getText());
					i++;
				} else if (jobInfos.get(i).getAttribute("class").equals("job-id")) {
					job.setReferenceId(jobInfos.get(i).getText().split(":")[1].trim());
					i = i + 2;
				} else if (jobInfos.get(i).getAttribute("class").equals("job-date")) {
					job.setPostedDate(parseDate(jobInfos.get(i).getText().split(":")[1].trim(), DF));
					if (job.getPostedDate() == null) log.info(" failed to parse date value " + jobInfos.get(i).getText().split(":")[1].trim() + " for job " + job.getUrl());
					i = i + 2;
				} else {
					i++;
					continue;
				}
			}
			Thread.sleep(5000);
			try {
			saveJob(getJobDetail(job), site);
			}catch(Exception e) {
				exception = e;
			}
			driver.executeScript("window.history.go(-1)");
			jobList = wait.until(
					ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//li[@class = 'jobs-list-item']"), 0));
		}
	}

	private Job getJobDetail(Job job) {
		driver.get(job.getUrl());
		WebElement elDes = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'jd-info au-target']")));
		job.setSpec(elDes.getText().trim());
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
