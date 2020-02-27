package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
 * Discover Financial job site parsing class. <br>
 * URL: https://jobs.discover.com/job-search
 * 
 * @author tanmoy.tushar
 * @since 2019-02-27
 */
@Service
@Slf4j
public class DiscoverFinancial extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DISCOVER_FINANCIAL;
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private String baseUrl;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 30);
		this.baseUrl = site.getUrl().substring(0, 25);
		driver.get(site.getUrl());
		int totalPage = getTotalPages();
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/job-search/?pg=" + i;
			try {
				browseJobList(site, url);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalPages() {
		WebElement totalJob = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@id='live-results-counter']")));
		expectedJobCount = Integer.parseInt(totalJob.getText());
		return getPageCount(totalJob.getText(), 12);
	}

	private void browseJobList(SiteMetaData site, String url) throws PageScrapingInterruptedException {
		driver.get(url);
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='jobTitle']/a")));
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(jobList.get(i).getAttribute("href"));
			try {
				 saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("h2[id=job-title]");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[id=JobFunction]");
		if (jobE != null) job.setCategory(jobE.text());
		jobE = doc.selectFirst("span[class=location]");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("div[class=description-left]");
		job.setSpec(jobE.text());
		jobE = doc.selectFirst("span[id=open-date]");
		job.setPostedDate(parseDate(jobE.text(), DF1, DF2));
		jobE = doc.selectFirst("span[id=job-id]");
		job.setReferenceId(jobE.text());
		jobE = doc.selectFirst("a[class=apply-btn]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
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