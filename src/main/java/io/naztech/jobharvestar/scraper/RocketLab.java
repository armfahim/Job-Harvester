package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
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
 * Rocket Lab jobsite pareser.<br>
 * URL: https://www.rocketlabusa.com/careers/positions/
 * 
 * @author Rahat Ahmad
 * @since 2019-03-31
 */
@Slf4j
@Service
public class RocketLab extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ROCKET_LAB;
	private String baseUrl;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<Job> jobList = getSummaryPage(siteMeta.getUrl());
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
			Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_4S));
		}
		driver.quit();
	}

	private List<Job> getSummaryPage(String url) throws InterruptedException {
		List<WebElement> jobListE = getAlljob(url);
		List<Job> jobList = new ArrayList<>();
		for (WebElement webElement : jobListE) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setUrl(webElement.getAttribute("href"));
			job.setTitle(webElement.findElement(By.className("job__title")).getText());
			job.setName(job.getTitle());
			job.setLocation(webElement.findElement(By.className("job__location")).getText());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) throws IOException {
		Document document = Jsoup.connect(job.getUrl()).get();
		try {
			job.setSpec(document.select("div.job__info-subtitle").text());
			job.setReferenceId(document.select("p.job__hero-description").text().split(Pattern.quote("|"))[0]
					.replace("Job Ref: ", "").trim());
			job.setType(document.select("p.job__hero-description").text().split(Pattern.quote("|"))[1]
					.replace("Type: ", "").trim());
		} catch (NullPointerException e) {
			log.warn("Failed to parse job detail page of " + job.getUrl(), e);
		}
		return job;
	}

	private List<WebElement> getAlljob(String url) throws InterruptedException {
		driver.get(url);
		for (;;) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			if (driver.findElements(By.xpath("//button[@id='JobsAjaxBtn']")).isEmpty())
				break;
			driver.findElement(By.id("JobsAjaxBtn")).click();
			Thread.sleep(TIME_4S);
		}
		return driver.findElements(By.xpath("//a[@class='job']"));
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
