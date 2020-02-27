package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
 * Dbs Group Holdings job site parser. <br>
 * URL:
 * https://careers.dbs.com/careersection/dbs_professional_hires_career_section/jobsearch.ftl#
 *
 * @author naym.hossain
 * @author iftekar.alam
 * @author tanmoy.tushar
 * @since 2019-02-20
 */
@Slf4j
@Service
public class DbsGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DBS_GROUP_HOLDINGS;
	private String baseUrl;
	private int expectedJobCount;
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM d yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM dd yyyy");
	private ChromeDriver driver;
	private WebDriverWait wait;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 60);
		List<String> jobUrl = new ArrayList<>();
		driver.get(siteMeta.getUrl());
		log.info("Page loading for more jobs, it will take time...");
		int totalPage = getTotalPage();
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				jobUrl.addAll(getSummaryPages());
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + driver.getCurrentUrl(), e);
			}
			if (i == totalPage - 1)	break;
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.id("next"))).click();
			} catch (Exception e) {
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", wait.until(ExpectedConditions.presenceOfElementLocated(By.id("next"))));
			}
			Thread.sleep(RandomUtils.nextInt(TIME_5S, TIME_10S*3));
		}
		log.info("Valid link found: " + jobUrl.size());
		for (String link : jobUrl) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(link);
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
		driver.quit();
	}

	private List<String> getSummaryPages() throws InterruptedException {
		List<String> jobLinks = new ArrayList<>();
		for (WebElement el : getJobList()) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = el.getAttribute("href");
			jobLinks.add(jobUrl);
		}
		return jobLinks;
	}

	private Job getJobDetails(Job job) {
		driver.get(job.getUrl());
		wait = new WebDriverWait(driver, 30);
		List<WebElement> elList = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='contentlinepanel']")));
		String[] titleRef = elList.get(0).getText().split("-");
		job.setTitle(titleRef[0].trim());
		int titleRefSize = titleRef.length;
		if (titleRefSize == 2) {
			job.setReferenceId(titleRef[1].replace("(", "").replace(")", "").trim());
		} else if (titleRefSize > 2) {
			job.setTitle(job.getTitle() + " - " + titleRef[1].trim());
			job.setReferenceId(titleRef[titleRefSize - 1].replace("(", "").replace(")", "").trim());
		}
		job.setName(job.getTitle());
		try {
			job.setSpec(elList.get(1).getText());
		} catch (IndexOutOfBoundsException e) {
		}
		try {
			if (elList.size() == 7) {
				job.setLocation(elList.get(2).getText().split(":")[1].trim());
				job.setCategory(elList.get(3).getText().split(":")[1].trim());
				job.setType(elList.get(5).getText().trim());
				job.setPostedDate(parseDate(
						elList.get(6).getText().split("Posting:")[1].substring(0, 13).replace(",", "").trim(), DF1, DF2));
			} else {
				if (elList.get(2).getText().contains("Location"))
					job.setLocation(elList.get(2).getText().split(":")[1].trim());
				if (elList.get(3).getText().contains("Job"))
					job.setCategory(elList.get(3).getText().split(":")[1].trim());
				if (elList.get(elList.size() - 2).getText().contains("-time"))
					job.setType(elList.get(elList.size() - 2).getText().trim());
				if (elList.get(elList.size() - 1).getText().contains("Posting"))
					job.setPostedDate(parseDate(elList.get(elList.size() - 1).getText().split("Posting:")[1]
							.substring(0, 13).replace(",", "").trim(), DF1, DF2));
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return job;
	}
	
	private List<WebElement> getJobList() {
		return wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"jobs\"]/tbody/tr/th/div/div/span/a"), 0));
	}
	
	private int getTotalPage() {
		getJobList();
		WebElement el = driver.findElement(By.id("currentPageInfo"));
		String totalJob = el.getText().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 25);
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
