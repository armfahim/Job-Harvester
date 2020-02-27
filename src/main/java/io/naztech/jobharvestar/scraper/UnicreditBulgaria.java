package io.naztech.jobharvestar.scraper;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Unicredit Bulgaria<br>
 * URL: https://careers.unicreditbulbank.bg/?sap-language=DE&sap-wd-configId=/UCIHR/HRRCF_A_UNREG_JOB_SEARCH#
 * 
 * @author naym.hossain
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
@Slf4j
public class UnicreditBulgaria extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNICREDIT_BULGARIA;

	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final int WAIT_DURATION_SEC = 50;

	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(WAIT_DURATION_SEC, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 70);
		startSiteScrapping(getSiteMetaData(ShortName.UNICREDIT_BULGARIA));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<Job> jobList = new ArrayList<>();
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		driver.get(siteMeta.getUrl());
		wait.until(presenceOfElementLocated(By.id("pagination")));
		jobList.addAll(getSummaryPage());

		List<WebElement> list = driver.findElements(By.className("step"));
		list.get(0).click();
		wait.until(presenceOfElementLocated(By.id("pagination")));

		int track = 3;
		while (true) {
			boolean flag = true;
			list = driver.findElements(By.className("step"));
			for (int i = 1; i < list.size()-1; i++) {
				int pageNumber = Integer.parseInt(list.get(i).getText());
				if (pageNumber == track) {
					flag = false;
					track++;
					list.get(i).click();
					wait.until(presenceOfElementLocated(By.id("pagination")));
					jobList.addAll(getSummaryPage());
					break;
				}
			}
			if (flag) break;
		}
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped())throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}
	
	private List<Job> getSummaryPage() throws InterruptedException {
		List<Job> jobSummaryList = new ArrayList<>();
		List<WebElement> jobList = driver.findElements(By.xpath("//table[@id='table-uni']/tbody/tr"));
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped())throw new PageScrapingInterruptedException();
			WebElement link = jobList.get(i).findElement(By.tagName("a"));
			Job job = new Job(link.getAttribute("href"));
			job.setName(link.getText());
			job.setTitle(link.getText());
			job.setCategory(jobList.get(i).findElements(By.tagName("td")).get(1).getText());
			job.setReferenceId(jobList.get(i).findElements(By.tagName("td")).get(2).getText());
			job.setLocation(jobList.get(i).findElements(By.tagName("td")).get(3).getText());
			job.setType(jobList.get(i).findElements(By.tagName("td")).get(4).getText());
			jobSummaryList.add(job);

		}
		return jobSummaryList;
	}

	private Job getJobDetail(Job job) {

		try {
			driver.get(job.getUrl());
			WebElement spec = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='position_descr']")));
			job.setSpec(spec.getText().trim());
			WebElement prerequisite = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='position_descr']")));
			job.setPrerequisite(prerequisite.findElements(By.tagName("div")).get(2).getText().trim());
			List<WebElement> postedDate = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//p[@class='fleft position_p']"), 0));
			job.setPostedDate(parseDate(postedDate.get(0).findElements(By.tagName("span")).get(0).getText().trim(), DF));
			job.setDeadline(parseDate(postedDate.get(0).findElements(By.tagName("span")).get(2).getText().trim(), DF));
//			if (job.getPostedDate() == null)
//				log.info(" failed to parse date value "
//						+ postedDate.get(0).findElements(By.tagName("span")).get(0).getText().trim() + " for job "
//						+ job.getUrl());
			WebElement appUrl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='submit_button mt15']")));
			job.setApplicationUrl(appUrl.getAttribute("href"));
		} catch (FailingHttpStatusCodeException e) {
			log.warn("Failed to parse job details ",driver.getCurrentUrl() + e);
		}
		return job;
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