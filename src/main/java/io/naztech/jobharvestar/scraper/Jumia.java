package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
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
 * Jumia jobs site parse <br>
 * URL: https://group.jumia.com/careers/#/department
 * 
 * @author tanmoy.tushar
 * @since 2019-03-12
 */
@Slf4j
@Service
public class Jumia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.JUMIA;
	private static final String ROW_LIST_PATH = "//div[@class='how-departments']/div/a";
	private WebDriverWait wait;
	private ChromeDriver driver;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 60);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver.get(site.getUrl());
		List<WebElement> rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			rowList.get(i).click();
			Thread.sleep(TIME_1S);
			List<WebElement> jobE = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//a[@class='offer']")));
			getSummaryPage(jobE, site);
			WebElement backE = wait.until(presenceOfElementLocated(By.xpath(
					"//a[@class='back vc_general vc_btn3 vc_btn3-size-lg vc_btn3-shape-square vc_btn3-style-flat vc_btn3-color-warning']")));
			backE.click();
			Thread.sleep(TIME_1S);
			rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
		}
		driver.quit();
	}

	private void getSummaryPage(List<WebElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		expectedJobCount += jobList.size();
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(jobList.get(i).getAttribute("href"));
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("div[class=columns col60]");
			job.setSpec(jobE.text());
			jobE = doc.selectFirst("p[id=pJobTitle]");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("p[id=pLocation]");
			if (jobE != null)
				job.setLocation(jobE.text());
			jobE = doc.selectFirst("p[id=pDepartment]");
			if (jobE != null)
				job.setCategory(jobE.text());
			return job;
		} catch (IOException | NullPointerException e) {
			log.warn(" Failed parse job details of " + job.getUrl(), e);
		}
		return null;
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