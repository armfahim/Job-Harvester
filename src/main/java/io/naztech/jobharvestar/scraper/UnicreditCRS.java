package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Unicredit Czech Republic and Slovakia URL:
 * https://unicreditbank.topjobs.sk/
 * 
 * @author Benajir Ullah
 * @author rahat.ahmad
 * @since 2019-02-03
 */
@Slf4j
@Service
public class UnicreditCRS extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNICREDIT_CZECH_REPUBLIC_AND_SLOVAKIA;
	private static ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl());
		List<Job> jobList = getAllJobListE();
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
			}
		}
		driver.quit();
	}

	private List<Job> getSummaryPage(List<WebElement> jobListE) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		for (WebElement webElement : jobListE) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			try {
				job.setUrl(webElement.getAttribute("href"));
				job.setTitle(webElement.findElement(By.className("jobs-list__title")).getText());
				job.setName(job.getTitle());
				job.setLocation(webElement.findElements(By.className("jobs-list__column")).get(2).getText());
				job.setType(webElement.findElements(By.className("jobs-list__column")).get(3).getText());
				job.setCategory(webElement.findElements(By.className("jobs-list__column")).get(1).getText());
			} catch (NoSuchElementException e) {
				log.warn("Failed to parse job summary of " + job.getTitle(), e);
			}
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetail(Job job) throws InterruptedException {
		driver.get(job.getUrl());
		try {
			job.setSpec(driver.findElements(By.xpath("//div[@class='cse-cont cse-detail-wrap']")).get(0).getText());
		} catch (NoSuchElementException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
		}
		return job;
	}
	
	private List<Job> getAllJobListE() throws InterruptedException {
		for (;;) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			if (driver.findElementsByXPath("//a[@class='button jobs__btn js-jobs__more']").size() == 0)
				break;
			driver.findElementsByXPath("//a[@class='button jobs__btn js-jobs__more']").get(0).click();
			Thread.sleep(TIME_1S * 4);
		}
		return getSummaryPage(driver.findElementsByXPath("//a[@class='jobs-list__row']"));
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
