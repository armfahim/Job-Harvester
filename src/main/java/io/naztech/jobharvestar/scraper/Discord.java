package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Discord job site parsing class. <br>
 * URL: https://discordapp.com/jobs
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-13
 */
@Slf4j
@Service
public class Discord extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DISCORD;
	private String baseUrl;
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		driver.get(siteMeta.getUrl());
		List<WebElement> jobLink = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='flexWrap-1tCbUz']/div/a")));
		List<String> allJobLink = new ArrayList<>();
		for (int i = 0; i < jobLink.size(); i++) {
			String Link = jobLink.get(i).getAttribute("href");
			if (Link.contains("/jobs/")) {
				allJobLink.add(Link);
			}
		}
		expectedJobCount = allJobLink.size();
		for (int i = 0; i < allJobLink.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(allJobLink.get(i));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed parse job details " + job.getUrl() + e);
			}
		}
		driver.quit();
	}

	private Job getJobDetail(Job job) throws IOException {
		driver.get(job.getUrl());
		wait = new WebDriverWait(driver, 50);
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("description-1gVGxt")));
		job.setSpec(el.getText());
		String title = driver.getTitle();
		job.setTitle(title.replace("Discord Inc. -", "").trim());
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
