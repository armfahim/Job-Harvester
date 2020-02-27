package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Lyft jobs site parse <br>
 * URL: https://www.lyft.com/careers
 * 
 * @author tanmoy.tushar
 * @since 2019-03-13
 */
@Service
@Slf4j
public class Lyft extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.LYFT;

	private WebDriverWait wait;
	private ChromeDriver driver;
	private int expectedJobCount = 0;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 60);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver.get(site.getUrl());
		List<WebElement> rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//button[@class='_3z7aC3']")));
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			rowList.get(i).click();
			Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_4S));
		}
		List<WebElement> jobE = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//a[@class='_1HcI2i']")));
		expectedJobCount=jobE.size();
		getSummaryPage(jobE, site);
		driver.quit();
	}

	private void getSummaryPage(List<WebElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(jobList.get(i).getAttribute("href"));
			try {
				saveJob(getJobDetails(job), site);	
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h1[class=app-title]");
			if (jobE != null) {
				job.setTitle(jobE.text());
				job.setName(job.getTitle());
			}
			jobE = doc.selectFirst("div[class=location]");
			if (jobE != null)
				job.setLocation(jobE.text());
			jobE = doc.selectFirst("div[id=content]");
			if (jobE != null)
				job.setSpec(jobE.text());
			job.setApplicationUrl(job.getUrl() + "#app");
			return job;
		} catch (IOException e) {
			log.warn("Failed parse job details of " + job.getUrl(), e);
		}
		return null;
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