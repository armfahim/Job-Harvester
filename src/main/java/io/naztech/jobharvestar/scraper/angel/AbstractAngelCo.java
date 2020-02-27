package io.naztech.jobharvestar.scraper.angel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Abstract for Angel sites <br>
 * Rally-rd URL: https://angel.co/rally-rd/jobs<br>
 * Hometap URL: https://angel.co/hometap/jobs<br>
 * synapseFi URL: https://angel.co/synapsefi/jobs<br>
 * TravelBank URL: https://angel.co/travelbank/jobs<br>
 * Alan URL: https://angel.co/alan-25/jobs<br>
 * Spruce URL: https://angel.co/spruce-8/jobs<br>
 * 
 * @author jannatul.maowa
 * @since 2019-03-31
 */
@Service
public abstract class AbstractAngelCo extends AbstractScraper implements Scrapper {

	private String baseUrl;
	private final Logger log = LoggerFactory.getLogger(getClass());
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 40);
		client = getChromeClient();
		client.getOptions().setThrowExceptionOnScriptError(false);
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled())
			log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			List<String> jobListA = new ArrayList<>();
			driver.get(siteMeta.getUrl());
			List<WebElement> jobList = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='listing-title s-grid-colSm18']/a")));
			for (WebElement jobL : jobList) {
				jobListA.add(jobL.getAttribute("href"));
			}
			expectedJobCount = expectedJobCount + jobList.size();
			for (String str : jobListA) {
				Job job = new Job(str);
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to save job", e);
				}
			}
		} catch (ElementNotFoundException e) {
			log.warn(siteMeta.getUrl() + "Error in pasring job Link" + e);
			throw e;
		}
	}

	private Job getJobDetails(Job job) throws IOException, InterruptedException {
		try {
			driver.get(job.getUrl());
			WebElement el = wait
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@class='u-colorGray3']")));
			job.setTitle(el.getText().split("at")[0].trim());
			job.setName(job.getTitle());
			el = driver.findElementByXPath("//div[@class='company-summary s-grid-colSm24']/div");
			job.setLocation(el.getText().split("·")[0].trim());
			job.setType(el.getText().split("·")[1].trim());
			el = driver.findElementByXPath("//div[@class='listing showcase-section u-bgWhite']");
			job.setSpec(el.getText().trim());
		} catch (ElementNotFoundException e) {
			log.warn("failed to parse job Details" + e, job.getUrl());
			return null;
		}
		return job;
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
