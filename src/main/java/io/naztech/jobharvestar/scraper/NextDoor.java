package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
 * Nextdoor job site parser<br>
 * URL: https://nextdoor.com/jobs/
 * 
 * @author Asadullah Galib
 * @author fahim.reza
 * @since 2019-03-11
 */
@Slf4j
@Service
public class NextDoor extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NEXTDOOR;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 110);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl());
		Thread.sleep(TIME_4S);
		driver.switchTo().frame(0);
		List<String> jobUrl = getSummaryPage();
		expectedJobCount = jobUrl.size();
		for (String string : jobUrl) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(string), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
		driver.quit();
	}

	private List<String> getSummaryPage() {
		List<String> jobUrl = new ArrayList<>();
		try {
			List<WebElement> jobLinks = wait.until(
					ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//section[@class='level-0']/div/a")));
			for (WebElement webElement : jobLinks) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				jobUrl.add(webElement.getAttribute("href"));
			}
		} catch (PageScrapingInterruptedException e) {
			log.warn("Failed to parse Site: " + getSiteName(), e);
		}
		return jobUrl;
	}

	private Job getJobDetails(String url) {
		Job job = new Job();
		job.setUrl(url);
		try {
			driver.get(url);
			WebElement el = driver.findElementById("grnhse_iframe");
			driver.get(el.getAttribute("src"));
			job.setTitle(driver.findElement(By.className("app-title")).getText());
			job.setName(job.getTitle());
			job.setLocation(driver.findElement(By.className("location")).getText());
			job.setSpec(driver.findElement(By.id("content")).getText());

		} catch (NoSuchElementException | TimeoutException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
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
