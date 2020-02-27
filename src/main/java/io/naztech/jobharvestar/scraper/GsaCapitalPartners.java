package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

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
 * GSA Capital Partners job site parsing class. <br>
 * URL: https://www.gsacapital.com/careers/#/
 * 
 * @author Rahat Ahmad
 * @since 2019-03-07
 */
@Service
public class GsaCapitalPartners extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.GSA_CAPITAL_PARTNERS;
	private static final String TOTAL_JOB = "//div[@class='_job col-xs-12 col-sm-6 col-md-4 col-lg-3 ng-tns-c5-0 ng-star-inserted']";
	private String baseUrl;
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<WebElement> jobLinks = getSummarypage(siteMeta);
		expectedJobCount = jobLinks.size();
		driver.findElement(By.className("cc-btn")).click();
		for(WebElement webElement : jobLinks) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
			saveJob(getJobDetail(webElement), siteMeta);
			}catch(Exception e) {
				exception = e;
			}
			jobLinks = getSummarypage(siteMeta);
			Thread.sleep(TIME_5S);
		}
		driver.quit();
	}
	
	private List<WebElement> getSummarypage(SiteMetaData siteMeta){
		driver.get(siteMeta.getUrl());
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(TOTAL_JOB)));
	}
	
	private Job getJobDetail(WebElement webElement) throws InterruptedException {
		Job job = new Job();
		webElement.findElement(By.tagName("button")).click();
		Thread.sleep(TIME_5S);
		job.setUrl(driver.findElement(By.tagName("iframe")).getAttribute("src"));
		driver.switchTo().frame(0);
		job.setTitle(driver.findElement(By.className("app-title")).getText());
		job.setName(job.getTitle());
		job.setLocation(driver.findElement(By.className("location")).getText());
		job.setSpec(driver.findElement(By.id("content")).getText());
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
