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
 * BlackRock A. job site parser <br>
 * URL: https://careers.blackrock.com/job-search-results/
 * 
 * @author naym.hossain
 * @author tanmoy.tushar
 * @author bm.alamin
 * @since 2019-03-13
 * @since 2019-04-17
 */
@Service
@Slf4j
public class BlackRock extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BLACKROCK_A;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String baseUrl;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(3, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		this.baseUrl = site.getUrl().substring(0, 29);
		int totalPage = getTotalPages(site.getUrl());
		List<WebElement> jobList;
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/job-search-results?pg=" + i;
			try {
				driver.get(url);
				jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul[@class='job-innerwrap']")));
				browseJobList(jobList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page " + url, e);
			}
		}
	}

	private void browseJobList(List<WebElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			WebElement jobTitle = el.findElement(By.xpath("//div[@class='jobTitle']/a"));
			Job job = new Job(jobTitle.getAttribute("href"));
			job.setTitle(jobTitle.getText().trim());
			job.setName(job.getTitle());
			job.setLocation(el.findElement(By.xpath("//li[@title='Location']/div")).getText().trim());
			job.setCategory(el.findElement(By.xpath("//li[@title='Teams']")).getText().trim());
			job.setPostedDate(parseDate(el.findElement(By.xpath("//li[@title='Posted Date']")).getText().trim(), DF));
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("a[class=white-button]");
		if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		jobE = doc.getElementById("gtm-jobdetail-desc");
		job.setSpec(jobE.text());
		jobE = doc.selectFirst("div[id='viewjobdetail'] > p");
		return job;
	}

	private int getTotalPages(String url) {
		driver.get(url);
		WebElement jobCount = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("live-results-counter")));
		String totalJob = jobCount.getText().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 12);
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
