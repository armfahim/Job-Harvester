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
 * BGL Group jobs site parser <br>
 * URL: https://www.bglgroup.co.uk/careers/search-and-apply?searchtext=&loc=0
 * 
 * @author tanmoy.tushar
 * @since 2019-03-11
 */
@Service
@Slf4j
public class BglGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BGL_GROUP;
	private static final String ROW_LIST = "//div[@class='js-job-box-wrapper']/a";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	private ChromeDriver driver;
	private WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		driver.get(site.getUrl());
		this.baseUrl = site.getUrl().substring(0, 26);
		WebElement nextE;
		int totalPage = getTotalPages();
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				browseJobList(site, rowList);
				if (i == totalPage - 1) break;
				nextE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='pg-next']")));
				JavascriptExecutor js = driver;
				js.executeScript("arguments[0].click();", nextE);
				Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_10S));
				rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
			} catch (Exception e) {
				log.warn("Failed to parse job list page no " + (i + 1), e);
			}
		}
		driver.quit();
	}

	private void browseJobList(SiteMetaData site, List<WebElement> rowList) throws PageScrapingInterruptedException {
		for (WebElement row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(row.getAttribute("href"));
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of" + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[class=job-salary]");
		if (jobE != null) job.setCategory(jobE.text());
		jobE = doc.selectFirst("div[class=job-detail job-location]");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("a[id=ctl00_cphMain_lnkApply]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("div[class=job-detail job-type]");
		if (jobE != null) job.setType(jobE.text());
		jobE = doc.selectFirst("div[class=job-description]");
		job.setSpec(jobE.text());
		return job;
	}

	private int getTotalPages() {
		WebElement jobCount = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='js-result-count']")));
		String totalJob = jobCount.getText().split(" ")[0];
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 18);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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