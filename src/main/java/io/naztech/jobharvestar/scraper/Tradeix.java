package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Tradeix job site scraper.<br>
 * URL: https://jobbio.com/tradeix
 * 
 * @author Asadullah Galib
 * @author iftekar.alam
 * @since 2019-03-27
 */
@Slf4j
@Service
public class Tradeix extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TRADEIX;
	private String baseUrl;
	private static WebDriverWait wait;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
		driver.get(siteMeta.getUrl());
		this.baseUrl = siteMeta.getUrl().substring(0, 18);
		WebElement nextE;
		while (true) {
			try {
				nextE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("channel-view-more")));
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", nextE);
				Thread.sleep(TIME_5S);
			} catch (Exception e) {
				break;
			}
		}
		for (String url : browseJobList()) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(url), siteMeta);
			} catch (Exception e) {  
				exception = e;
				log.warn("Failed to parse detaitsPage of " + url,e);
			}
		}
	}
	private List<String> browseJobList() throws PageScrapingInterruptedException {
		List<String> jobUrlList = new ArrayList<>();
		List<WebElement> rowList = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='row boxed boxed--border ember-view']")));
		expectedJobCount=rowList.size();
		for (WebElement el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			jobUrlList.add(el.getAttribute("href"));
		}		
		return jobUrlList;
	}

	private Job getJobDetails(String url) {
		Job job = new Job(url);
		driver.get(job.getUrl());
		WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("wrap")));
		job.setTitle(title.getText());
		job.setName(title.getText());
		WebElement spec = driver.findElementByClassName("job-cards");
		job.setSpec(spec.getText());
		List<WebElement> location = driver.findElementsByClassName("color--primary");
		job.setLocation(location.get(1).getText());
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