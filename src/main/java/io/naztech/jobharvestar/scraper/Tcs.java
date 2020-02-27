package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
 * TCS job site parsing class. <br>
 * URL: https://ibegin.tcs.com/iBegin/jobs/search
 * 
 * @author iftekar.alam
 * @author tanmoy.tushar
 * @since 2019-10-23
 */
@Service
@Slf4j
public class Tcs extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TCS;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private static final String ROW_LIST = "//div[@class='job-data-bar']";
	private static final String NEXT_BUTTON_LIST = "//ul[@class='pagination']/li/a";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 120);
		driver.get(site.getUrl());
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
		getJobDetail(rowList, site);
		int totalPage = getTotalPage();
		List<WebElement> nextclick = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(NEXT_BUTTON_LIST)));
		for (int i = 2; i <= totalPage; i++) {
			try {
				int k = i/5;
				if (i % 5 == 0) k -= 1;
				clickingLimitation(k, nextclick);
				
				for (WebElement el : nextclick) {
					try {
						if (i == Integer.parseInt(el.getText().trim())) {
							try {
								el.click();
							} catch (Exception e) {
								JavascriptExecutor js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].click();", el);
							}
						}
					} catch (NumberFormatException e) {
						continue;
					}
				}
				
				Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_4S * 2));
				rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
				
				try {
					getJobDetail(rowList, site);
				} catch (Exception e) {
					log.warn("Failed to parse job list page", e);
				}
				nextclick = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(NEXT_BUTTON_LIST)));
				
				if (i % 5 == 0) {
					k = i/5;
					clickingLimitation(k, nextclick);
					rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
					try {
						getJobDetail(rowList, site);
					} catch (Exception e) {
						log.warn("Failed to parse job list page", e);
					}
					i++;
				}
				
				nextclick = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(NEXT_BUTTON_LIST)));
			} catch (Exception e) {
				log.warn("Exception Occured in page " + i, e);
				rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
				nextclick = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(NEXT_BUTTON_LIST)));
			}			
		}
	}
	
	private void clickingLimitation(int k, List<WebElement> nextclick) throws InterruptedException {
		for (int j = 0; j < k; j++) {
			clickForNextFive(nextclick);
		}
	}
	
	private void clickForNextFive(List<WebElement> nextclick) throws InterruptedException {
		for (WebElement el : nextclick) {
			try {
				if (el.getText().trim().equals("›")) {
					try {
						el.click();
					} catch (Exception e) {
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("arguments[0].click();", el);
					}
				}
			} catch (NumberFormatException e) {
				continue;
			}
		}
		Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_4S * 2));
	}

	private void getJobDetail(List<WebElement> rowList, SiteMetaData site) throws InterruptedException {
		for (int j = 0; j < rowList.size(); j++) {
			if (j == 0) {
				rowList.get(j).findElements(By.tagName("div")).get(0).findElements(By.tagName("a")).get(0).click();
				Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_4S * 2));
			}
			Job job = new Job(driver.getCurrentUrl());
			try {
				WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@data-ng-bind='jobDescription.title']")));
				job.setTitle(title.getText().trim());
				job.setName(job.getTitle());
				WebElement jobT = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='job-description-content']")));
				job.setSpec(jobT.getText().trim());
				List<WebElement> rowList5 = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='row custom-row description-location']/span")));
				job.setLocation(rowList5.get(0).getText().trim().split(" ")[0].trim());
				WebElement dateTime = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@data-ng-bind='jobApplyDate']")));
				job.setPostedDate(parseDate(dateTime.getText().trim(), DF));
				List<WebElement> Ref = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@data-ng-bind='jb.pair']")));
				job.setCategory(Ref.get(0).getText().trim());
				job.setReferenceId(Ref.get(2).getText().trim());
				saveJob(job, site);
				if(j == rowList.size() - 1) break;
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
			WebElement nextJobButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='description-arrow-right ng-scope']/img")));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", nextJobButton);
			Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_4S * 2));
		}
		WebElement backButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='inline-div backFAQ']")));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", backButton);
		Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_4S * 2));
	}

	private int getTotalPage() throws InterruptedException {
		List<WebElement> row = wait.until(presenceOfAllElementsLocatedBy(
				By.xpath("//div[@class='job-search-heading-h2 centered-text font-20-regular bot-mar-20']/span/span")));
		String totalJob = row.get(0).getText().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
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