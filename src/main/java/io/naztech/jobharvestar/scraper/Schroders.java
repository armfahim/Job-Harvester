package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Schroders job site parsing class. <br>
 * URL: https://schroders.referrals.selectminds.com/careers
 * 
 * @author assaduzzaman.sohan
 * @author tanmoy.tushar
 * @since 2019-02-19
 */
@Service
@Slf4j
public class Schroders extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SCHRODERS;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 15);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		List<String> catUrl = new ArrayList<>();
		try {
			driver.get(site.getUrl());
			List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='category_tile_title']/h4/a")));
			for (WebElement row : rowList) {
				catUrl.add(row.getAttribute("href"));
			}
			for (String url : catUrl) {
				try {
					browseJobList(url, site);
				} catch (Exception e) {
					log.warn("Failed to parse category job of " + url, e);
				}
			}
		} catch (TimeoutException e) {
			log.error("Failed to parse job", e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws InterruptedException {
		driver.get(url);
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='job_link font_bold']")));
		int i = 0;
		do {
			driver.executeScript("window.scrollBy(0,document.body.scrollHeight)");
			Thread.sleep(TIME_1S);
		} while (++i < 10);
		rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='job_link font_bold']")));
		expectedJobCount += rowList.size();
		for (WebElement el : rowList) {
			String jobUrl = el.getAttribute("href");
			try {
				saveJob(getJobDetail(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job list of " + jobUrl, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).timeout(TIME_10S).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("h4[class=primary_location]");
		if (jobE != null) job.setLocation(jobE.text().substring(2).trim());
		jobE = doc.selectFirst("div[class=job_description]");
		job.setSpec(jobE.text());
		jobE = doc.selectFirst("dd[class=job_post_date]>span");
		if (jobE != null) {
			try {
				job.setPostedDate(parseAgoDates(jobE.text()));				
			} catch (DateTimeParseException e) {
				job.setPostedDate(parseDate(jobE.text(), DF));
			}
		}
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
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
