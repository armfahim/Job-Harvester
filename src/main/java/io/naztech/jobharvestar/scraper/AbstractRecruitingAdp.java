package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All job site of recruiting adp parsing class. <br>
 * https://recruiting.adp.com/srccar/public/RTI.home?c=1047945&d=HuntingtonExternal
 * https://recruiting.adp.com/srccar/public/RTI.home?d=AllyCareers&c=1125607
 * 
 * @author rahat.ahmad
 * @author tanmoy.tushar
 * @since 2019-02-27
 */
@Service
public abstract class AbstractRecruitingAdp extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String ROW_LIST_PATH = "//div[@class='slidedown']";
	protected WebDriverWait wait;
	protected ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled())
			log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 90);
		driver.get(site.getUrl());
		List<WebElement> jobListE = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
		for (int i = 0; i < getTotalPage(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPage(jobListE, site);
			WebElement nextPageB = wait.until(presenceOfElementLocated(By.xpath("//div[@class='pageNum']/button[2]")));
			nextPageB.click();
			Thread.sleep(TIME_4S);
			jobListE = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
		}
		driver.quit();
	}

	private void getSummaryPage(List<WebElement> jobListE, SiteMetaData site) {
		try {
			for (int i = 0; i < jobListE.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				WebElement titleE = jobListE.get(i).findElement(By.tagName("a"));
				Job job = new Job();
				job.setTitle(titleE.getText());
				job.setName(job.getTitle());

				/* Click for go to job details page, used JavaScript Executor for click */
				JavascriptExecutor exe = (JavascriptExecutor) driver;
				exe.executeScript("arguments[0].click();", titleE);
				Thread.sleep(TIME_5S);

				WebElement jobE = wait.until(
						presenceOfElementLocated(By.xpath("//div[@class='jobdetails-container col-sm-12 no-margin']")));
				try {
					saveJob(getJobDetails(jobE, job), site);					
				} catch(Exception e) {
					exception = e;
					log.warn("Failed parse job details of " + job.getTitle(), e);
				}

				/* Click for move to job list page */
				jobE = wait.until(presenceOfElementLocated(By.xpath("//div[@class='col-sm-12 back2results']/button")));
				exe.executeScript("arguments[0].click();", jobE);
				Thread.sleep(TIME_5S);
				jobListE = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
			}
		} catch (InterruptedException e) {
			log.info("Unable to parse job", e);
		}
	}

	protected abstract Job getJobDetails(WebElement jobE, Job job);

	private int getTotalPage() {
		String totalJob = driver.findElement(By.xpath("//span[@class='currentCount']")).getText().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
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