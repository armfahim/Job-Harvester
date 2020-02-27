package io.naztech.jobharvestar.scraper;

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

/**
 * Krungsri job site parsing class. <br>
 * URL: https://www.krungsri.com/bank/en/Corporate-Info/Jobs-with-Us/Career-Search-Sub.html#
 * 
 * @author tanmoy.tushar
 * @since 2019-04-03
 */
@Service
public class Krungsri extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MUFG_KRUNGSRI;
	private static final String JOB_LIST_PATH = "//span[@id='creDiv']/div";

	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount = 0;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 10);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		driver.get(site.getUrl());
		int totalPage = getTotalPage();
		List<WebElement> jobList = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_LIST_PATH)));
		int count = 0;
		for (int i = 0; i < totalPage; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			getSummaryPages(site, jobList, count);
			WebElement nextBtn = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("//span[@class='_wPaginate_link _wPaginate_link_next']")));
			nextBtn.click();
			jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_LIST_PATH)));
			count = count + 10;
		}
		driver.quit();
	}

	private void getSummaryPages(SiteMetaData site, List<WebElement> jobList, int count)
			throws PageScrapingInterruptedException {
		expectedJobCount += jobList.size();
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setTitle(jobList.get(i).getText().trim());
			jobList.get(i).click();
			WebElement jobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("step" + count)));
			try {
				saveJob(getJobDetails(job, jobE), site);				
			} catch (Exception e) {
				exception = e;
			}
			count++;
		}
	}

	private int getTotalPage() {
		List<WebElement> paginationL = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='_wPaginate_holder _wPaginate_hwc_bay']/span")));
		String totalPage = paginationL.get(paginationL.size() - 3).getText();
		return Integer.parseInt(totalPage);
	}

	private Job getJobDetails(Job job, WebElement jobE) throws PageScrapingInterruptedException {
		job.setSpec(jobE.getText());
		job.setUrl(getJobHash(job));
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