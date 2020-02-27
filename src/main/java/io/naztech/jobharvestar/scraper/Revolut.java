package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
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
 * Revolut Job Site Parser. <br>
 * URL: https://www.revolut.com/careers/all
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-03-31
 */
@Service
public class Revolut extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.REVOLUT;
	private static final String ROW_LIST = "//div[@class='styles__StyledFullTitle-ybkzcl-1 efShWF']";
	private String baseUrl;
	private ChromeDriver driver;
	private static WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 20);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl());
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST + "/strong")));
		expectedJobCount = jobLinks.size();
		getSummaryPage(jobLinks, siteMeta);
		driver.quit();
	}

	private void getSummaryPage(List<WebElement> jobLinks, SiteMetaData site) throws InterruptedException {
		List<WebElement> jobCatLoc = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST + "/span")));
		List<WebElement> jobDetailsBtn = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
				By.xpath("//div[@class='styles__StyledQuestionControl-sc-4bugcf-0 hvytlA']")));
		int j = 0;
		int k = 1;
		for (int i = 0; i < jobLinks.size(); i++) {
			Job job = new Job();
			job.setTitle(jobLinks.get(i).getText());
			job.setName(job.getTitle());
			job.setCategory(jobCatLoc.get(j).getText());
			job.setLocation(jobCatLoc.get(k).getText());
			j += 2;
			k += 2;
			jobDetailsBtn.get(i).click();
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
			try {
				saveJob(getJobDetails(job, i), site);
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job, int i) {
		List<WebElement> jobAppUrl = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
				By.xpath("//a[@class='styles__StyledButton-ethqdb-0-a styles__StyledAnchorButton-ethqdb-1 DBdGp']")));
		job.setApplicationUrl(jobAppUrl.get(i).getAttribute("href"));
		List<WebElement> jobSpec = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
				By.xpath("//div[@class='styles__StyledCareerDescription-bc32yy-3 jRDtEy']")));
		job.setSpec(jobSpec.get(i).getText());
		job.setUrl(driver.getCurrentUrl());
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
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
