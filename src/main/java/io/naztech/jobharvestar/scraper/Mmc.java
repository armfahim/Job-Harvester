package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
 * MARSH & MCLENNAN COS Job Site Parser. <br>
 * URL: https://careers.mmc.com/search-jobs
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-03-10 
 */
@Slf4j
@Service
public class Mmc extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MARSH_N_MCLENNAN_COS;
	private static final int JOB_PER_PAGE = 15;
	private String baseUrl;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM. dd, yyyy");
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver.get(site.getUrl());
		Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_4S));
		try {
			driver.findElement(By.id("gdpr-button")).click();
		} catch (NoSuchElementException e) {
			log.warn("Cookie enable button not appear", e);
			throw e;
		}
		int totalPage = getTotalPage();
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//section[@id='search-results-list']/ul/li")));
		for (int i = 1; i <= totalPage; i++) {
			try {
				getSummaryPage(jobList, site);
				if (i == totalPage) break;
				driver.findElement(By.className("next")).click();
				Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_5S));
				jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//section[@id='search-results-list']/ul/li")));
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + driver.getCurrentUrl(), e);
			}			
		}
		driver.quit();
	}

	private void getSummaryPage(List<WebElement> jobList, SiteMetaData site) {
		for (WebElement el : jobList) {
			Job job = new Job(el.findElement(By.tagName("a")).getAttribute("href"));
			try {				
				job.setTitle(el.findElement(By.tagName("h2")).getText());
				job.setName(job.getTitle());
				job.setLocation(el.findElements(By.tagName("span")).get(1).getText());
				WebElement date = el.findElements(By.tagName("span")).get(2);
				job.setPostedDate(parseDate(date.getText(), DF));
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("div[class=ats-description]");
		job.setSpec(jobE.text());
		jobE = doc.selectFirst("span[class=job-id job-info]");
		if (jobE != null) job.setReferenceId(jobE.text().substring(7));
		jobE = doc.selectFirst("a[class=button job-apply top]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		return job;
	}
	
	private int getTotalPage() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@role='status']")));
		String totalJob = el.getText().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOB_PER_PAGE);
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
