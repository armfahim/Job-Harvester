package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
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
import lombok.extern.slf4j.Slf4j;

/**
 * ViceMedia Job Site Parser<br>
 * URL: https://company.vice.com/careers/job-board/#.WbFWJRNSz-Z
 * 
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @author fahim.reza
 * @author iftekar.alam
 * @since 2019-03-13
 */
@Slf4j
@Service
public class ViceMedia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.VICE_MEDIA;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	public static WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 20);
		driver.get(siteMeta.getUrl());
		List<Job> rowList = new ArrayList<>();
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='mw-main mxa pb4']/a")));
		expectedJobCount = jobList.size();
		rowList.addAll(getSummaryPages(siteMeta, jobList));
		for (Job job : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse detail page of " + job.getUrl(), e);
			}
		}
	}
	
	private List<Job> getSummaryPages(SiteMetaData site, List<WebElement> jobList)throws PageScrapingInterruptedException {
		List<Job> jobUrl = new ArrayList<>();
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			jobUrl.add(job);
		}
		return jobUrl;
	}

	private Job getJobDetails(Job job) throws InterruptedException {
		driver.get(job.getUrl());
		job.setTitle(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//header[@class='mxa mw-main px4 py4']/h1"))).getText());
		job.setName(job.getTitle());
		job.setLocation(driver.findElement(By.xpath("//section[@class='notes-container c4 px2 py2']/section[1]/div[2]")).getText());
		job.setCategory(driver.findElement(By.xpath("//section[@class='notes-container c4 px2 py2']/section[2]/div[2]")).getText());
		job.setSpec(driver.findElement(By.xpath("//section[@class='c8 roles py2']/div[2]")).getText());
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
