package io.naztech.jobharvestar.scraper.selenium;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

import java.time.format.DateTimeFormatter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Use this abstract for all the classes which need to use web driver for browse job list. <br>
 * And also need web driver for parse job details.
 * 
 * @author tanmoy.tushar
 * @since 2019-04-25
 */
@Service
public abstract class AbstractSeleniumJobList extends AbstractScraper implements Scrapper {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected static ChromeDriver driver;
	protected WebDriverWait wait;
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
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		try {
			driver.get(site.getUrl());
			List<WebElement> rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath(getRowListPath())));
			List<Job> jobList = browseJobList(rowList);
			expectedJobCount = jobList.size();
			log.info("Total job found: " + expectedJobCount);
			for (Job job : jobList) {
				try {
					saveJob(getJobDetail(job), site);					
				} catch(Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			}
		} catch (TimeoutException e) {
			log.info("Failed to prepare job list", e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private List<Job> browseJobList(List<WebElement> rowList) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		log.info("Page loading for more jobs. It will take time...");
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			/* Collecting job url from row list */
			Job job = new Job(rowList.get(i).getAttribute("href"));
			if (getFirstPageCatPath() != null) {
				List<WebElement> catList = driver.findElements(By.xpath(getFirstPageCatPath()));
				job.setCategory(catList.get(i).getText());
			}
			if (getFirstPageLocPath() != null) {
				List<WebElement> locList = driver.findElements(By.xpath(getFirstPageLocPath()));
				job.setLocation(locList.get(i).getText());
			}
			jobList.add(job);
		}
		return jobList;
	}

	protected Job getJobDetail(Job job) {
		try {
			driver.get(job.getUrl());
			WebElement jobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(getTitleXPath())));
			job.setTitle(jobE.getText());
			job.setName(job.getTitle());
			job.setSpec(getElementText(getSpecXPath()));
			if (getLocationXPath() != null)
				job.setLocation(getElementText(getLocationXPath()));
			if (getCategoryXPath() != null)
				job.setCategory(getElementText(getCategoryXPath()));
			if (getJobTypeXPath() != null)
				job.setType(getElementText(getJobTypeXPath()));
			if (getRefXPath() != null)
				job.setReferenceId(getElementText(getRefXPath()));
			if (getPreReqXPath() != null)
				job.setPrerequisite(getElementText(getPreReqXPath()));
			if (getPostedDateXPath() != null)
				job.setPostedDate(parseDate(getElementText(getPostedDateXPath()), getDateFormats()));
			if (getApplyUrlXPath() != null) {
				jobE = driver.findElement(By.xpath(getApplyUrlXPath()));
				String appUrl = getBaseUrl() != null ? getBaseUrl() : "";
				appUrl += jobE.getAttribute("href");
				job.setApplicationUrl(appUrl);
			}
			return job;
		} catch (NoSuchElementException | TimeoutException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
		}
		return job;
	}

	private String getElementText(String elementXPath) {
		WebElement el = driver.findElement(By.xpath(elementXPath));
		return el == null ? null : el.getText().trim();
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

	/* Always provide rowList path to collect job link */
	protected abstract String getRowListPath();
	protected abstract String getFirstPageCatPath();
	protected abstract String getFirstPageLocPath();
	protected abstract String getTitleXPath();
	protected abstract String getLocationXPath();
	protected abstract String getCategoryXPath();
	protected abstract String getJobTypeXPath();
	protected abstract String getRefXPath();
	protected abstract String getSpecXPath();
	protected abstract String getPreReqXPath();
	protected abstract String getPostedDateXPath();
	protected abstract String getApplyUrlXPath();
	protected abstract DateTimeFormatter[] getDateFormats();
}
