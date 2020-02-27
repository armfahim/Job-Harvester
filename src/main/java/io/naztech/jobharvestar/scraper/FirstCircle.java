package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * FirstCircle job site parser.<br>
 * URL: https://firstcircle.bamboohr.com/jobs/
 * 
 * @author Shadman Shahriar
 * @author tanmoy.tushar
 * @since 2019-03-27
 */
@Slf4j
@Service
public class FirstCircle extends AbstractScraper implements Scrapper {
	private final String SITE = ShortName.FIRST_CIRCEL;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(3, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 15);
		driver.get(siteMeta.getUrl());
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ResAts__listing-link")));
		expectedJobCount = rowList.size();
		browseJobList(rowList, siteMeta);
	}

	private void browseJobList(List<WebElement> rowList, SiteMetaData siteMeta) throws InterruptedException {
		for (WebElement el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = el.getAttribute("href");
			try {
				saveJob(getJobDetails(url), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetails(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h2");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[class=ResAts__card-text]");
		String[] parts = jobE.text().trim().split(" – ");
		if (parts.length > 1) {
			job.setCategory(parts[0].trim());
			job.setLocation(jobE.text().trim());
		}
		jobE = doc.selectFirst("div[class=ResAts__Viewport js-jobs-viewport js-chosen-container]");
		job.setSpec(doc.selectFirst("div[class=ResAts__Viewport js-jobs-viewport js-chosen-container]").text().trim());
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
		driver.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
