package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

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
 * <a href="https://www.avaloq.com/en/open-positions/">Avaloq Group job
 * parser</a>
 * 
 * @author Shajedul Islam
 * @since 2019-03-11
 */
@Service
@Slf4j
public class AvaloqGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AVALOQ_GROUP;
	private String baseUrl = "https://www.avaloq.com/en";
	private static final String TOTAL_JOBS = "//div[@class='row avlq-list-item avlq-list-collapsible-item']";
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			driver.get(siteMeta.getUrl());
			Thread.sleep(4000);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,500)");
			Thread.sleep(TIME_1S);
			driver.findElement(By.id("_it_smc_liferay_privacy_web_portlet_PrivacyPortlet_okButton")).click();
			List<WebElement> jobElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(TOTAL_JOBS)));
			expectedJobCount = jobElements.size();
			for (WebElement jobElement : jobElements) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(jobElement), siteMeta);					
				} catch(Exception e) {
					exception = e;
				}
			}
		} catch (Exception e) {
			log.warn("Failed to parsing job with Exception: " + e);
			throw e;
		}finally {
			driver.quit();
		}
	}

	private Job getJobDetails(WebElement jobElement) throws InterruptedException {
		Job job = new Job();
		try {
			jobElement.click();
			Thread.sleep(TIME_1S);
			job.setTitle((jobElement.findElements(By.tagName("p")).get(0)).getText());
			job.setName(job.getTitle());
			job.setLocation((jobElement.findElements(By.tagName("p")).get(1)).getText());
			job.setCategory((jobElement.findElements(By.tagName("p")).get(2)).getText());
			job.setSpec((jobElement.findElements(By.className("col-xs-12")).get(3)).getText());
			job.setApplicationUrl((jobElement.findElements(By.tagName("a")).get(1)).getAttribute("href"));
			job.setUrl(getJobHash(job));
			jobElement.click();
			Thread.sleep(2000);
		} catch (Exception ex) {
			System.out.println("Failed to parse detail of a job");
		}
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
