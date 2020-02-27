package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
 * SquareSpace job site parser. <br>
 * URL: https://www.squarespace.com/about/careers
 * 
 * @author Shadman Shahriar
 * @since 2019-03-20
 */
@Slf4j
@Service
public class SquareSpace extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SQUARESPACE;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(150, TimeUnit.SECONDS);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			driver.get(siteMeta.getUrl());
			wait = new WebDriverWait(driver, TIME_10S);
			JavascriptExecutor js = driver;
			js.executeScript("window.scrollBy(0,1200)");
			List<WebElement> catListUrl = wait
					.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='grid-cell']")));
			List<String> urlToJobPage = new ArrayList<String>();
			List<String> categoryList = new ArrayList<String>();
			List<Integer> jobCount = new ArrayList<Integer>();
			for (WebElement webElement : catListUrl) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				urlToJobPage.add(webElement.getAttribute("href").trim());
				categoryList.add(webElement.findElement(By.className("cell-department")).getText().trim());
				jobCount.add(Integer.parseInt(webElement.findElement(By.className("cell-job-count")).getText().trim()));
			}
			browseJobList(urlToJobPage, categoryList, jobCount, siteMeta);
		} catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
			log.warn("Failed to find element in page: " + driver.getCurrentUrl(), e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private void browseJobList(List<String> urlToJobPage, List<String> categoryList, List<Integer> jobCount,
			SiteMetaData siteMeta) throws InterruptedException {
		try {
			JavascriptExecutor js = driver;
			for (int i = 0; i < urlToJobPage.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				if (jobCount.get(i) > 0) {
					driver.get(urlToJobPage.get(i));
					wait = new WebDriverWait(driver, TIME_10S);
					js.executeScript("window.scrollBy(0,2000)");
					List<WebElement> jobLinkUrl = wait.until(
							ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//a[@class='list-item']"), 0));
					List<String> jobUrl = new ArrayList<String>();
					List<String> jobTitle = new ArrayList<String>();
					List<String> jobLocation = new ArrayList<String>();
					for (WebElement webElement2 : jobLinkUrl) {
						if (isStopped()) throw new PageScrapingInterruptedException();
						jobUrl.add(webElement2.getAttribute("href").trim());
						jobTitle.add(webElement2.findElement(By.className("item-title")).getText().trim());
						jobLocation.add(webElement2.findElement(By.className("item-location")).getText().trim());
					}
					expectedJobCount += jobLinkUrl.size();
					getJobDetails(jobUrl, jobTitle, jobLocation, categoryList.get(i), siteMeta);
				}
			}
		} catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
			log.warn("Failed to find element in page: " + driver.getCurrentUrl(), e);
		}
	}

	private void getJobDetails(List<String> url, List<String> title, List<String> location, String category,
			SiteMetaData siteMeta) throws InterruptedException {
		Job job = new Job();
		try {
			for (int i = 0; i < url.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					driver.get(url.get(i));
					wait = new WebDriverWait(driver, TIME_1M);
					driver.switchTo().defaultContent();
					driver.switchTo().frame("grnhse_iframe");
					job.setUrl(url.get(i));
					job.setTitle(title.get(i));
					job.setName(job.getTitle());
					job.setLocation(location.get(i));
					job.setCategory(category);
					job.setSpec(driver.findElement(By.id("content")).getText().trim());
					saveJob(job, siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (ArrayIndexOutOfBoundsException | NoSuchElementException e) {
			log.debug("Failed to parse job details of " + job.getUrl(), e);
		}
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
