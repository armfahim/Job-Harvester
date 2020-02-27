package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ElementNotFoundException;
import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Uber job parsing class<br>
 * URL: https://www.uber.com/us/en/careers/list/
 * 
 * @author tanmoy.tushar
 * @author bm.alamin
 * @since 2019-04-06
 * 
 */
@Service
@Slf4j
public class Uber extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UBER;
	private static WebDriver driver;
	private static WebDriverWait wait;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 30);
		driver.get(site.getUrl());
		this.baseUrl = site.getUrl().substring(0, 20);
		int totalJob = getTotalJob();
		expectedJobCount = totalJob;
		log.info("Page loading for more jobs. It will take time...");
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@class='os']/a")));
		while (true) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				try {
					getShowMoreButton().click();
				} catch (Exception e) {
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", getShowMoreButton());
				}
				Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S*3));
			} catch (TimeoutException e) {
				break;
			}
			jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@class='os']/a")));
		}
		log.info("Total Job Link Found: " + jobList.size());
		browseJobList(jobList, site);
		driver.quit();
	}

	private void browseJobList(List<WebElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = jobList.get(i).getAttribute("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_10S*2).get();
		Job job = new Job(url);
		Element jobE = doc.selectFirst("h1");
		if (jobE == null) throw new ElementNotFoundException("Failed to fetch job list page " + url);
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[class=bp be fq cx ou kj]");
		if (jobE != null) {
			String[] parts = jobE.text().split(" in ");
			if (parts.length == 2) {
				job.setCategory(parts[0].trim());
				job.setLocation(parts[1].trim());
			}
		}
		jobE = doc.selectFirst("div[class=ow ox]>a");
		if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		jobE = doc.selectFirst("div[class=bp fq]");
		job.setSpec(jobE.text());
		return job;
	}

	public WebElement getShowMoreButton() {
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='qr qs qt ct k5 qu qv']")));
	}

	public int getTotalJob() {
		WebElement totalJob = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='q8 q9 qa']/div/p")));
		return Integer.parseInt(totalJob.getText().split(" ")[0].trim());
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
