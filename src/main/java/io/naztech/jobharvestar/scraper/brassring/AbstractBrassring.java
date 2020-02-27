/**
 * 
 */
package io.naztech.jobharvestar.scraper.brassring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
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
 * All job sites of https://sjobs.brassring.com parsing abstract class <br>
 * 
 * AMERIPRISE FINANCIAL URL:
 * https://sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=25678&siteid=5275#keyWordSearch=&locationSearch=
 * EVEREST GROUP URL:
 * https://sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=25713&siteid=5365#home
 * PEOPLES UNITED FINANCIAL URL:
 * https://sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=25679&siteid=5313#home
 * PNC FINL SERVICES GROUP URL:
 * https://sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=15783&siteid=5130#keyWordSearch=&locationSearch=
 * TOTAL URL:
 * https://krb-sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=30080&siteid=6558#Language=All&keyWordSearch=
 * UBS URL:
 * https://jobs.ubs.com/TGnewUI/Search/Home/Home?partnerid=25008&siteid=5012&PageType=searchResults&SearchType=linkquery&LinkID=3108#home
 * 
 * @author tanmoy.tushar
 * @since 2019-02-26
 */
@Service
public abstract class AbstractBrassring extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String JOB_ROW_LIST_PATH = "//div[@class='liner lightBorder']";
	private static final String MORE_JOBS_ID = "showMoreJobs";
	private static final String JOB_SEARCH_BTN = "//div[@class='searchControls']/button";

	public static ChromeDriver driver;
	public static WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 50);
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		List<String> jobUrl = new ArrayList<>();
		try {
			driver.get(site.getUrl());
			WebElement search = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(JOB_SEARCH_BTN)));
			search.click();
			
			/* Theard.sleep() used for preparing job list
			 * 
			 * Different sites need different site to load job list page
			 * May be you found job list with less time but 
			 * it doesn't ensure all the time you will get correct list 
			 */
			Thread.sleep(TIME_10S * 5);
			
			List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_ROW_LIST_PATH)));
			int totalPage = getTotalPage(); 
			int totalJob = getExpectedJob();
			log.info("Job available in site: " + totalJob);
			if(totalJob > 500) log.info("Page loading for more jobs, it will take time...");			
			
			for (int j = 0; j < totalPage - 1; j++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				WebElement moreJobs = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(MORE_JOBS_ID)));
				try {
					moreJobs.click();
				} catch (ElementClickInterceptedException e) {
					JavascriptExecutor executor = (JavascriptExecutor)driver;
					executor.executeScript("arguments[0].click();", moreJobs);
				}
				Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
				rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_ROW_LIST_PATH)));
			}
			
			log.info("Job Link Found: " + rowList.size());
			jobUrl.addAll(browseJobList(rowList, site));
			log.info("Valid Link Found: " + jobUrl.size());
			
			for(String link : jobUrl) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(link, driver, wait), site);					
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job detail page of " + link, e);
				}
			}
		} catch (TimeoutException e) {
			log.info("Failed to load page", e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private List<String> browseJobList(List<WebElement> rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		List<String> jobLink = new ArrayList<>();
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				WebElement jobUrl = driver.findElement(By.id("Job_" + i));
				jobLink.add(jobUrl.getAttribute("href"));
			} catch (NoSuchElementException e) {
				log.info("Job link isn't available for job no " + (i + 1));
			}
		}
		return jobLink;
	}

	private int getTotalPage() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='sectionHeading']/h2")));
		String totalJob = el.getText().trim().split(" ")[0];
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 50);
	}

	protected abstract Job getJobDetails(String jobUrl, ChromeDriver driver, WebDriverWait wait);

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
