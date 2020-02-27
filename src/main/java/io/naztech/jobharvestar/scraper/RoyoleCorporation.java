package io.naztech.jobharvestar.scraper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
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

/**
 * Royole Corporation job site parsing class. <br>
 * URL: https://www.royole.com/en/join-us
 * 
 * @author tanmoy.tushar
 * @since 2019-03-27
 */
@Service
public class RoyoleCorporation extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ROYOLE_CORPORATION;

	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 10);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		driver.get(site.getUrl());
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//p[@class='enht']")));
		expectedJobCount = rowList.size();
		getSummaryPages(site, rowList);
		driver.quit();
	}

	private void getSummaryPages(SiteMetaData site, List<WebElement> rowList) throws InterruptedException {
		for (int i = 0; i < rowList.size(); i++) {
			Job job = new Job();
			job.setTitle(rowList.get(i).getText());
			job.setName(job.getTitle());
			rowList.get(i).click();
			Thread.sleep(TIME_4S);
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;				
			}
			rowList.get(i).click();
			Thread.sleep(TIME_1S);
		}
	}

	private Job getJobDetails(Job job) throws PageScrapingInterruptedException {
		WebElement jobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='enbody']")));
		job.setSpec(jobE.getText());
		jobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='btn-ca']")));
		job.setApplyEmail(jobE.getAttribute("href"));
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