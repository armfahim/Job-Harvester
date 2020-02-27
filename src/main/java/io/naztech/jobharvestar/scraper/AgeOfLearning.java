package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Age of Learning job site parsing class. <br>
 * URL: https://www.ageoflearning.com/careers/#jobvite
 * 
 * @author tanmoy.tushar
 * @since 2019-03-27
 */
@Service
public class AgeOfLearning extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AGE_OF_LEARNING;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 10);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		driver.get(site.getUrl());
		List<WebElement> rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//td[@class='jv-job-list-name']/a")));
		expectedJobCount = rowList.size();
		getSummaryPages(site, rowList);
		driver.quit();
	}

	private void getSummaryPages(SiteMetaData site, List<WebElement> rowList) throws InterruptedException {
		for(int i = 0; i< rowList.size(); i++) {
			Job job = new Job();
			job.setTitle(rowList.get(i).getText());
			job.setName(job.getTitle());
			try {
				rowList.get(i).click();
			} catch(WebDriverException e) {
				i++;
				rowList.get(i).click();
			}
			try {
				saveJob(getJobDetails(job, i), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job, int i) throws InterruptedException {
		List<WebElement> jobInfoE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//p[@class='jv-job-detail-meta']")));
		job.setCategory(jobInfoE.get(i).getText().split("Glendale")[0]);
		try {
			job.setLocation(jobInfoE.get(i).getText().split(job.getCategory())[1]);
		} catch (IndexOutOfBoundsException e) {
			/* Intentionally blank */
		}
		jobInfoE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='jv-job-detail-description']")));
		job.setSpec(jobInfoE.get(i).getText());
		jobInfoE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='jv-button jv-button-primary jv-button-apply']")));
		job.setApplicationUrl(jobInfoE.get(i).getAttribute("href"));
		job.setUrl(getJobHash(job));
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