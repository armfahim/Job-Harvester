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

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Capital Float jobs site parser <br>
 * Url: https://capitalfloat.darwinbox.in/jobs
 * 
 * @author fahim.reza
 * @since 2019-03-31
 */
@Service
@Slf4j
public class CapitalFloat extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CAPITAL_FLOAT;

	private String baseUrl = null;
	protected ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 10);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws InterruptedException {
		try {
			driver.get(siteMeta.getUrl());
			List<WebElement> rowList = wait.until(
					ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//tr[@class='hover-icon-wrapper']"), 0));
			expectedJobCount = rowList.size();
			for (int i = 0; i < rowList.size(); i++) {
				Job job = new Job(rowList.get(i).findElement(By.tagName("a")).getAttribute("href"));
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
				driver.navigate().back();
				rowList = wait.until(ExpectedConditions
						.numberOfElementsToBeMoreThan(By.xpath("//tr[@class='hover-icon-wrapper']"), 0));
			}

		} catch (TimeoutException e) {
			log.info("Failed to load job list", e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		driver.get(job.getUrl());
		wait = new WebDriverWait(driver, 50);
		WebElement elTitle = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("display-2")));
		WebElement elLocation = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("mb-4")));
		List<WebElement> element = wait.until(ExpectedConditions
				.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='job-details-list']/ul/li")));
		WebElement spec = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("job-summary")));
		job.setTitle(elTitle.getText());
		job.setName(job.getTitle());
		job.setLocation(elLocation.getText());
		job.setCategory(element.get(0).findElement(By.tagName("p")).getText());
		String[] date = element.get(1).findElement(By.tagName("p")).getText().split(",");
		job.setPostedDate(parseDate(date[0] + "," + date[1], DF));
		job.setType(element.get(2).findElement(By.tagName("p")).getText());
		job.setSpec(spec.getText());
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
