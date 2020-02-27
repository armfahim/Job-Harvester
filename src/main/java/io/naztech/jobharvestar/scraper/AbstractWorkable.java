package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Abstract of workable site<br>
 * 
 * <ul>
 * <li><a href="https://apply.workable.com/riskalyze/">Riskalyze</a>
 * <li><a href="https://deposit-solutions.workable.com">DepositSolutions</a>
 * <li><a href="https://apply.workable.com/thehutgroup/">Thehutgroup</a>
 * <li><a href="https://apply.workable.com/lendinvest/">Lendinvest</a>
 * <li><a href="https://apply.workable.com/lendkey-technologies-inc/">Lendkey</a>
 * <li><a href="https://apply.workable.com/pony-dot-ai/">PonyAi</a>
 * 
 * <li><a href="https://complyadvantage.workable.com/">Complyadvantage</a>
 * 
 * <li><a href="https://truelayer.workable.com">Truelayer</a>
 * <li><a href="https://kantox.workable.com">Kantox</a>
 * </ul> 
 * @author rafayet.hossain
 * @author iftekar.alam
 * @since 2019-03-31
 */
public abstract class AbstractWorkable extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private int expectedJobCount;
	private Exception exception;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled())
			log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 50);
		try {
			driver.get(siteMeta.getUrl());
			log.info("Page loading for more jobs, it will take time...");
			WebElement nextE;
			while (true) {
				try {
					nextE = driver.findElement(By.xpath("//button[@class='_-_-shared-ui-atoms-button-base-___button__button _-_-shared-ui-atoms-button-base-___button__normal _-_-shared-ui-atoms-button-secondary-___secondary__default careers-jobs-list-loadmore-styles__loadmore--3-bqh']"));
				    nextE.click();
				} catch (Exception e) {
					break;
				}
				Thread.sleep(TIME_4S);
			}
			List<WebElement> rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//div[@class='_-_-shared-components-card-styles__content--3gX94']")));
			List<Job> jobList = browseJobList(rowList);
			expectedJobCount = jobList.size();
			log.info("Total job found: " + jobList.size());
			for (Job job : jobList) {
				try {
					saveJob(getJobDetails(job), siteMeta);					
				} catch(Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			}
		} catch (TimeoutException  e) {
			log.info("Failed to prepare job list", e);
		} 
	}
	
	
	protected List<Job> browseJobList(List<WebElement> rowList) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		try {
			for (int i = 0; i < rowList.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				/* Collecting job url from row list */
				Job job = new Job(rowList.get(i).findElement(By.tagName("a")).getAttribute("href"));
				String postDate=rowList.get(i).findElement(By.tagName("small")).getText().trim();
				if (postDate.contains("about")) job.setPostedDate(parseAgoDates(postDate.split("about")[1].trim()));
        		else job.setPostedDate(parseAgoDates(postDate.split("Posted")[1].trim()));
				jobList.add(job);
			}
		} catch (Exception e) {
			log.warn("falied to parse job list ",e);
		}
		return jobList;
	}


	protected Job getJobDetails(Job job) throws InterruptedException {
		driver.get(job.getUrl());
		Thread.sleep(TIME_4S);
		job.setTitle(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@data-ui='job-title']"))).getText().trim());
		job.setName(job.getTitle()); 
		 /*
		  *  NOTE: do not put log here. intentionally left blank
		  */
		try {
			job.setLocation(driver.findElement(By.xpath("//span[@data-ui='job-location']")).getText().trim());
		} catch (Exception e) {
		}
		try {
			job.setType(driver.findElement(By.xpath("//span[@data-ui='job-type']")).getText().trim());
		} catch (Exception e) {
		}
		try {
			job.setCategory(driver.findElement(By.xpath("//span[@data-ui='job-department']")).getText().trim());
		} catch (Exception e) {
		}
		try {
			job.setReferenceId(driver.findElement(By.xpath("//span[@data-ui='job-code']")).getText().trim());
		} catch (Exception e) {
		}
		job.setApplicationUrl(driver.findElement(By.xpath("//div[@class='job-preview-styles__applyButton--35QFH']/a")).getAttribute("href"));
		job.setSpec(driver.findElement(By.xpath("//div[@class='job-preview-styles__preview--2d3Fz']")).getText().trim());
		return job;
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
