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
 * Cadre job site parser.<br>
 * URL: https://cadre.com/careers
 * 
 * @author Shadman Shahriar
 * @since 2019-03-31
 */
@Slf4j
@Service
public class Cadre extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CADRE;
	private ChromeDriver driver;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			driver.get(siteMeta.getUrl());
			WebDriverWait wait = new WebDriverWait(driver, TIME_1M);
			List<WebElement> Btn = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
					By.xpath("//div[@class='StandardButton StandardButton--primary']")));
			JavascriptExecutor executor = driver;
			executor.executeScript("arguments[0].click();", Btn.get(0));
			List<WebElement> jobCategoryLink = driver
					.findElements(By.xpath("//ul[@class='JobPanel-module__departments__laBqj']/li"));
			browseJobList(jobCategoryLink, siteMeta);
		} catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
			log.warn("Element not found in "+ driver.getCurrentUrl(),e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private void browseJobList(List<WebElement> jobCategoryLink, SiteMetaData siteMeta) throws InterruptedException {
		try {
			List<List<String>> jobUrls = new ArrayList<>();
			int counter=0;
			for (WebElement jobCat : jobCategoryLink) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				String category = jobCat.findElement(By.tagName("h1")).getText().trim();
				List<WebElement> jobLink = jobCat.findElements(By.className("Careers-module__blueTextLink__10shD"));
				expectedJobCount += jobLink.size();
				for (WebElement url : jobLink) {
					if (isStopped()) throw new PageScrapingInterruptedException();
					jobUrls.add(new ArrayList<>());
					jobUrls.get(counter).add(category);
					jobUrls.get(counter).add(url.getAttribute("href"));
					counter++;
				}
			}
			getJobDetails(jobUrls, siteMeta);
		} catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
			log.warn("Element not found in "+ driver.getCurrentUrl(),e);
		}
	}

	private void getJobDetails(List<List<String>> jobUrls, SiteMetaData siteMeta) throws InterruptedException {
		try {
			for (int i = 0; i < jobUrls.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(jobUrls.get(i).get(1));
				driver.get(jobUrls.get(i).get(1));
				new WebDriverWait(driver, TIME_1M);
				driver.switchTo().defaultContent();
				driver.switchTo().frame("grnhse_iframe");
				try {
					job.setTitle(driver.findElement(By.className("app-title")).getText().trim());
					job.setName(job.getTitle());
					job.setCategory(jobUrls.get(i).get(0));
					job.setLocation(driver.findElement(By.className("location")).getText().trim());
					job.setSpec(driver.findElement(By.id("content")).getText().trim());
					saveJob(job, siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
			log.warn("Failed to parse job details of "+driver.getCurrentUrl(), e);
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
