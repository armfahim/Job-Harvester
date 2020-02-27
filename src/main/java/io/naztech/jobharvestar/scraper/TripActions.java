package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
 *  Trip Actions job site parser.<br>
 * 	URL: https://tripactions.com/job-openings
 * 	@author fahim.reza 
 * 	@since 2019-11-14
 *
 */
@Slf4j
@Service
public class TripActions extends AbstractScraper implements Scrapper {
	private final String SITE = ShortName.TRIPACTIONS;
	private String baseUrl;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	public static WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		driver.get(siteMeta.getUrl());
		List<WebElement> jobList = driver.findElements(By.xpath("//li[@class='posting']/a"));
		expectedJobCount = jobList.size();
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setUrl(baseUrl + jobList.get(i).getAttribute("innerText"));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
		}catch(NoSuchElementException e) {
			log.warn("Failed to load job list of "+siteMeta.getUrl(),e);
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element title = doc.selectFirst("h1");
		Element location = doc.selectFirst("h5");
		Elements spec = doc.select("div[class=col-md-6 col-xs-12]");
		job.setTitle(title.text());
		job.setLocation(location.text());
		job.setSpec(spec.text());
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
