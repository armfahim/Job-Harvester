package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.TimeoutException;
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
 * Infor jobs site parser <br>
 * URL: https://css-inforhcm-prd.inforcloudsuite.com/hcm/CandidateSelfService/controller.servlet?context.dataarea=inforhcm_prd_hcm&context.session.key.HROrganization=INFR&context.session.key.JobBoard=EXTERNAL#
 * 
 * @author tanmoy.tushar
 * @since 2019-03-12
 */
@Service
@Slf4j
public class Infor extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INFOR;
	private static final String ROW_LIST_PATH = "//div[@class='grid-canvas grid-canvas-top grid-canvas-left']/div";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");

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
		WebElement iframeE = wait.until(presenceOfElementLocated(By.id("parentIframe")));
		driver.switchTo().frame(iframeE);
		WebElement next;
		int totalPage = getTotalPages();
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
			getSummaryPage(rowList, site);
			if (i == totalPage - 1) break;
			next = wait.until(presenceOfElementLocated(By.xpath("//button[@class='inforGridPagingButton  nextPage']")));
			next.click();
		}
		driver.quit();
	}

	private void getSummaryPage(List<WebElement> rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		try {
			for (int i = 0; i < rowList.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				rowList.get(i).click();
				Thread.sleep(TIME_1S);
				WebElement jobE = wait.until(presenceOfElementLocated(By.xpath("//div[@class='colmask rightmenu']")));
				try {
					saveJob(getJobDetails(jobE), site);
				} catch (Exception e) {
					exception = e;
				}
				WebElement backE = wait.until(presenceOfElementLocated(By.id("BackJobListing3")));
				backE.click();
				Thread.sleep(TIME_1S);
				rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
			}
		} catch (InterruptedException | ElementNotVisibleException e) {
			log.warn("Exception Occured", e);
		}
	}

	private Job getJobDetails(WebElement jobE) {
		try {
			Job job = new Job();
			jobE = wait.until(presenceOfElementLocated(By.id("paperClipPosition")));
			job.setTitle(jobE.getText());
			job.setName(job.getTitle());
			jobE = wait.until(presenceOfElementLocated(By.id("summaryLocation")));
			job.setLocation(jobE.getText().trim());
			jobE = wait.until(presenceOfElementLocated(By.id("postDate")));
			job.setPostedDate(parseDate(jobE.getText(), DF));
			jobE = wait.until(presenceOfElementLocated(By.id("summaryCategoryDesc")));
			job.setCategory(jobE.getText());
			jobE = wait.until(presenceOfElementLocated(By.id("posdetails")));
			job.setSpec(jobE.getText());
			job.setUrl(getJobHash(job));
			return job;
		} catch (TimeoutException e) {
			log.warn(getSiteName() + " Failed parse job details", e);
		}
		return null;
	}

	public int getTotalPages() {
		WebElement el = wait.until(presenceOfElementLocated(By.xpath("//div[@class='slick-record-status']")));
		String totalJob = el.getText().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
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