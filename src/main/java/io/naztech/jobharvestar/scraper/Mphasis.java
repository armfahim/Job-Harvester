package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Mphasis job site parser. <br>
 * URL: https://careers.mphasis.com/home/jobs.html
 * 
 * @author tanmoy.tushar
 * @since 2019-10-20
 */
@Slf4j
@Service
public class Mphasis extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MPHASIS;
	private static final String ROW_LIST = "//a[@class='applyNow']";
	private static final String DESC_CMN_PATH = "//div[@id='banner-panel']/div";
	private int expectedJobCount;
	private Exception exception;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private WebClient client;

	@Override
	public void scrapJobs() throws Exception {
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(site.getUrl());
		client.waitForBackgroundJavaScript(TIME_10S * 2);
		List<HtmlElement> jobList = page.getByXPath(ROW_LIST);
		expectedJobCount = jobList.size();
		browseJobList(jobList, site);
	}

	private void browseJobList(List<HtmlElement> rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 15);
		for (HtmlElement el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = el.getAttribute("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		driver.get(job.getUrl());
		WebElement titleE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@class='job-title']")));
		job.setTitle(titleE.getText().trim());
		job.setName(job.getTitle());
		job.setSpec(wait.until(ExpectedConditions.presenceOfElementLocated(By.id("job-desc"))).getText().trim());
		try {
			job.setLocation(driver.findElement(By.xpath(DESC_CMN_PATH + "/ul/li/strong[1]")).getText().trim());
			String refId = driver.findElement(By.xpath(DESC_CMN_PATH + "/ul/li[3]")).getText();
			if (refId.contains("ID")) job.setReferenceId(refId.split(":")[1].trim());
			if (refId.contains("Processing officer")) job.setReferenceId(refId.split("officer")[1].trim());
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			log.info("Location/Reference ID is not found for " + job.getTitle());
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
		client.close();
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
