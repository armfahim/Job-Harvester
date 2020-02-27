package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
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
 * Hannover Rueck job site parser.<br>
 * URL: https://jobs.hannover-re.com/?ac=search_result&_csrf_token
 * 
 * @author naym.hossain
 * @author tanmoy.tushar
 * @since 2019-02-26
 */
@Slf4j
@Service
public class HannoverRueck extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.HANNOVER_RUECK;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 90);
		driver.get(site.getUrl());
		int totalPage = getTotalPage();
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@class = 'table']/tbody/tr")));
			browseJobList(jobList, site);
			if (i == totalPage - 1)	break;
			List<WebElement> nextPages = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//ul[@class = 'pagination pagination-sm']/li"), 0));
			try {
				nextPages.get(6).findElement(By.tagName("a")).click();
			} catch (Exception e) {
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", nextPages.get(6).findElement(By.tagName("a")));
			}
			Thread.sleep(RandomUtils.nextInt(TIME_10S, TIME_10S * 5));
		}
	}

	private void browseJobList(List<WebElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.findElement(By.tagName("a")).getAttribute("href"));
			job.setTitle(el.findElement(By.tagName("a")).getText().trim());
			job.setName(job.getTitle());
			job.setLocation(el.findElements(By.tagName("td")).get(1).getText().trim());
			job.setCategory(el.findElements(By.tagName("td")).get(2).getText().trim());
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		job.setSpec(Jsoup.connect(job.getUrl()).get().getElementById("jobad_content").text().trim());
		return job;
	}
	
	private int getTotalPage() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='searchoptions']/h1")));
		String totalJob = el.getText().trim().split(":")[1].trim().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		System.out.println(expectedJobCount);
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
