package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Afiniti Job Site Parser<br>
 * URL: https://www.afiniti.com/careers
 * 
 * @author masum.billa
 * @author fahim.reza
 * @since 2019-03-13
 */
@Slf4j
@Service
public class Afiniti extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AFINITI;
	private String baseUrl;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	public static WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) {
		try {
			driver.get(siteMeta.getUrl());
			wait = new WebDriverWait(driver, 60);
			WebElement nextE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("gnewtonIframe")));
			Document doc = Jsoup.connect(nextE.getAttribute("src")).get();
			Elements jobUrl = doc.select("div[class=gnewtonCareerGroupJobTitleClass]>a");
			expectedJobCount = jobUrl.size();
			Job job = new Job();
			for (Element url : jobUrl) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				try {
					String link = url.attr("href");
					saveJob(getJobDetails(link, job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (InterruptedException | NoSuchElementException | FailingHttpStatusCodeException | IOException e) {
			log.warn(" failed to parse job Url page of " + getSiteName(), e);
		}
	}

	private Job getJobDetails(String string, Job job) throws IOException {
		job.setUrl(string);
		try {
			driver.get(string);
			WebElement nextE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("gnewtonIframe")));
			Document doc = Jsoup.connect(nextE.getAttribute("src")).get();
			job.setTitle(doc.getElementById("gnewtonJobPosition").text().split(":")[1].trim());
			job.setName(job.getTitle());
			job.setLocation(doc.getElementById("gnewtonJobLocationInfo").text().trim());
			job.setReferenceId(doc.getElementById("gnewtonJobID").text().split(":")[1].trim());
			job.setSpec(doc.getElementById("gnewtonJobDescriptionText").text());
		} catch (NoSuchElementException | TimeoutException e) {
			log.warn("Failed to parse job details of :" + job.getUrl(), e);
		}

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
