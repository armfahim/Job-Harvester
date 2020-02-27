package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
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
 * AXA Job Site Parser. <br>
 * URL: https://www.axa.com/en/careers/job-opportunities
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-03-05
 */
@Slf4j
@Service
public class Axa2 extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AXA2;
	private static final String HEAD = "https://www.axa.com/en/careers/job-opportunities#page=";
	private String baseUrl;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	private static final String LOAD_MORE = "//a[@class='button-secondary button-secondary--red job-board__job-list__load-more']";
	private static final String APPLICATION_URL = "//a[@class='job-details-description__button-block__button button-primary button-primary--red']";
	private static final String SPEC = "//div[@class='job-details-description__description__wrapper']";
	private static final String JOB_LIST_EL = "//div[@class='job-board__job-list']/div/div";
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver(false);
		log.info("Page loading for more jobs, it will take time...");	
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 60);
		this.baseUrl = siteMeta.getUrl().substring(0, 19);
		driver.get(siteMeta.getUrl());
		int totalPage = getTotalPage();
		loadMoreBtnClick();		
		List<WebElement> jobListE = getSummaryPages(totalPage);
		List<Job> jobList = new ArrayList<Job>();
		for (WebElement el : jobListE) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setTitle(el.findElements(By.tagName("span")).get(0).getText());
			job.setName(job.getTitle());
			job.setType(el.findElements(By.tagName("span")).get(1).getText());
			job.setLocation(el.findElements(By.tagName("span")).get(2).getText());
			job.setUrl(el.findElement(By.tagName("a")).getAttribute("href"));
			jobList.add(job);
		}
		expectedJobCount=jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
		driver.quit();
	}

	private List<WebElement> getSummaryPages(int totalPage) throws InterruptedException {
		List<WebElement> oldList = driver.findElements(By.xpath(JOB_LIST_EL));
		for (;;) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> newList = new ArrayList<>();
			try {
				loadMoreBtnClick();
				newList = driver.findElements(By.xpath(JOB_LIST_EL));
			} catch (WebDriverException e) {
				log.warn("Load jobs done", e);
				break;
			}
			if (newList.size() == oldList.size() || driver.getCurrentUrl().equals(HEAD + totalPage)) break;
			oldList = newList;
		}
		return driver.findElements(By.xpath(JOB_LIST_EL));
	}

	private Job getJobDetails(Job job) {
		try {
			driver.get(job.getUrl());
			WebElement spec = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(SPEC)));
			job.setSpec(spec.getText());
			job.setApplicationUrl(driver.findElement(By.xpath(APPLICATION_URL)).getAttribute("href"));
		} catch (NoSuchElementException | TimeoutException e) {
			log.warn("Spec not found of " + job.getUrl(), e);
		}
		return job;
	}
	
	private void loadMoreBtnClick() throws InterruptedException {
		WebElement loadMoreEl = driver.findElement(By.xpath(LOAD_MORE));
		JavascriptExecutor executor = driver;
		executor.executeScript("arguments[0].click();", loadMoreEl);
		Thread.sleep(TIME_1S * 30);
	}
	
	private int getTotalPage() {
		WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("job-board__job-counter__label")));
		String totalJob = element.getText().trim();
		return getPageCount(totalJob, 10);
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
