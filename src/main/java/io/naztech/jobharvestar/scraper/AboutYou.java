package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * About You job site parsing class. <br>
 * URL: https://corporate.aboutyou.de/de/career
 * 
 * @author tanmoy.tushar
 * @author bm.alamin
 * @since 2019-03-25
 */
@Service
@Slf4j
public class AboutYou extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ABOUT_YOU;
	private static final String ROW_LIST_PATH = "//ul[@class='lazyList__list positionsList__list']/li/a";
	private static final String BUTTON_PATH = "//button[@class='button button--bordered lazyList__pagination lazyList__pagination--mobile']";
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 30);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		List<Job> jobList = new ArrayList<>();
		try {
			driver.get(site.getUrl());
			List<WebElement> rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
			WebElement btnE = wait.until(presenceOfElementLocated(By.xpath(BUTTON_PATH)));
			while (true) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				btnE.click();
				rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
				try {
					WebElement btnLastE = driver.findElement(By.xpath("//button[@disabled='disabled']"));
					if (btnLastE != null)
						break;
				} catch (NoSuchElementException e) {
					continue;
				}
				Thread.sleep(TIME_1S);
			}
			jobList.addAll(getSummaryPages(site, rowList));
			expectedJobCount = jobList.size();
			for (Job job : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(job), site);					
				} catch(Exception e) {
					exception = e;
				}
			}
		} catch (ElementNotVisibleException e) {
			log.info(getSiteName() + " Element Not Visible Exception Occured" + e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private List<Job> getSummaryPages(SiteMetaData site, List<WebElement> rowList)
			throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		List<WebElement> titleL = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//div[@class='position__title']")));
		List<WebElement> catL = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//span[@class='position__department']")));
		List<WebElement> locL = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//span[@class='position__location']")));
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(rowList.get(i).getAttribute("href"));
			
			job.setTitle(titleL.get(i).getText());
			job.setName(job.getTitle());
			job.setCategory(catL.get(i).getText());
			job.setLocation(locL.get(i).getText());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) throws PageScrapingInterruptedException {
		try {
			driver.get(job.getUrl());
			WebElement appE = wait.until(presenceOfElementLocated(By.xpath("//div[@class='jobContacts__buttons']/a")));
			job.setApplicationUrl(appE.getAttribute("href"));
			List<WebElement> jobSpecL = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//ul[@class='jobDescription__requirements']")));
			if (jobSpecL.size() == 2) {
				job.setSpec(jobSpecL.get(0).getText());
				job.setPrerequisite(jobSpecL.get(1).getText());
			}
			return job;
		} catch (TimeoutException e) {
			try {
				List<WebElement> jobSpecL2 = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//div[@class='jobEmptyContainer']")));
				if (jobSpecL2.size() == 4) {
					job.setSpec(jobSpecL2.get(1).getText());
					job.setPrerequisite(jobSpecL2.get(2).getText());
				}
				return job;
			} catch(TimeoutException e1) {
				log.info(getSiteName() + " Failed parse job details of " + job.getUrl(), e);				
			}
			
		}
		return null;
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