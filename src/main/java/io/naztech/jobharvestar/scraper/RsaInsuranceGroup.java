package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * RSA Insurance Group<br>
 * URL: https://www.rsagroup.com/careers/search-our-vacancies/uk-job-list?
 * 
 * @author tohedul.islum
 * @since 2019-02-13
 */
@Service
@Slf4j
public class RsaInsuranceGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.RSA_INSURANCE_GROUP;
	private static final int JOBPERPAGE = 10;
	private String baseUrl;
	private static final String TAILURL = "/careers/search-our-vacancies/uk-job-list?page=";
	private static final int WAIT_DURATION_SEC = 50;

	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(WAIT_DURATION_SEC, TimeUnit.SECONDS);
		startSiteScrapping(getSiteMetaData(ShortName.RSA_INSURANCE_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		int totalPage = getTotalPages(siteMeta.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPages(getBaseUrl() + TAILURL + i, siteMeta);
		}
		driver.quit();
	}

	private int getTotalPages(String url)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(url);
		Thread.sleep(8000);
		wait = new WebDriverWait(driver, WAIT_DURATION_SEC);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class='table']/caption/span")));
		WebElement jobCount = driver.findElementByXPath("//table[@class='table']/caption/span");
		expectedJobCount = Integer.parseInt(jobCount.getText());
		return getPageCount(jobCount.getText(), JOBPERPAGE);
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException {
		driver.get(url);
		Thread.sleep(8000);
		wait = new WebDriverWait(driver, WAIT_DURATION_SEC);
		List<WebElement> jobList = wait.until(
				ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//table[@class='table']/tbody/tr"), 0));
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			WebElement link = jobList.get(i).findElement(By.tagName("a"));
			Job job = new Job(link.getAttribute("href"));
			job.setName(link.getText());
			job.setTitle(link.getText());
			job.setLocation(jobList.get(i).findElements(By.tagName("td")).get(0).getText());
			job.setCategory(jobList.get(i).findElements(By.tagName("td")).get(1).getText());
			job.setReferenceId(jobList.get(i).findElements(By.tagName("td")).get(2).getText());
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
			driver.executeScript("window.history.go(-1)");
			jobList = wait.until(
					ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//table[@class='table']/tbody/tr"), 0));
		}

	}

	private Job getJobDetail(Job job) throws InterruptedException {
		try {
			driver.get(job.getUrl());
			Thread.sleep(8000);
			wait = new WebDriverWait(driver, WAIT_DURATION_SEC);
			WebElement spec = wait.until(
					ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='lumesseJobDetailWidget']")));
			if (spec.findElements(By.tagName("p")).size() >= 2) {
				job.setSpec(spec.findElements(By.tagName("p")).get(0).getText()
						+ spec.findElements(By.tagName("p")).get(1).getText());
			} else {
				job.setSpec(spec.findElements(By.tagName("p")).get(0).getText());
			}
			WebElement appUrl = wait
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@role='navigation']")));
			job.setApplicationUrl(appUrl.findElements(By.tagName("a")).get(2).getAttribute("href"));
		} catch (FailingHttpStatusCodeException e) {
			log.warn("Failed to parse job Details" + job.getUrl(), e);
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
