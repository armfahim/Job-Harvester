package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
 * Transfer Wise Jobsite Parser<br>
 * URL: https://www.traveloka.com/en/careers
 * 
 * @author Fahim Reza
 * @author jannatul.maowa
 * @since 2019-03-14
 */
@Service
@Slf4j
public class TransferWise extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TRANSFERWISE;
	private static WebDriverWait wait;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 60);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			driver.get(siteMeta.getUrl());
			List<Job> jobLink = new ArrayList<>();
			WebElement search = wait
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@id='searchJob']")));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", search);
			Thread.sleep(TIME_5S);
			List<WebElement> row = wait
					.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='tv-job-list']")));
			expectedJobCount = row.size();
			for (int i = 0; i < row.size(); i++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(row.get(i).findElement(By.tagName("a")).getAttribute("href"));
				job.setTitle(row.get(i).findElements(By.tagName("a")).get(1).getText());
				job.setName(job.getTitle());
				job.setCategory(row.get(i).findElement(By.className("highlight")).getText());
				jobLink.add(job);
			}
			for (Job ob : jobLink) {
				try {
					saveJob(getJobDetails(ob), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}

		} catch (FailingHttpStatusCodeException e) {
			log.warn(SITE + " failed to connect site", e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	public Job getJobDetails(Job job) throws InterruptedException, IOException {
		try {
			driver.get(job.getUrl());
			wait = new WebDriverWait(driver, 50);
			Thread.sleep(TIME_5S);
			WebElement location = driver.findElementByXPath("//ul[@class='job-details']/li");
			try {
				job.setPrerequisite(driver.findElementByXPath("//section[@id='st-qualifications']").getText());
			} catch (Exception e) {
				// Intentionally left Blank
			}
			job.setLocation(location.getText());
			job.setType(driver.findElementByXPath("//ul[@class='job-details']/li[2]").getText());
			job.setSpec(driver.findElementByXPath("//section[@id='st-jobDescription']").getText());
		} catch (NoSuchElementException e) {
			log.warn(SITE + " Failed to parse job details of " + job.getUrl(), e);
		}
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
