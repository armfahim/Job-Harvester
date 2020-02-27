package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
 * Invesco job site parser. <br>
 * URL: https://careers.invesco.com/ListJobs
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-02-26
 */
@Service
@Slf4j
public class Invesco extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INVESCO_LTD;
	private static final String ROW_LIST_PATH = "//td[@class='JobTitle-cell']/a";
	private DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm:ss a");
	private DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("M/dd/yyyy h:mm:ss a");
	private DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("MM/d/yyyy h:mm:ss a");
	private DateTimeFormatter DF4 = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(TIME_1M * 2, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 60);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver.get(site.getUrl());
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
		int totalPage = getTotalPage();
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				browseJobList(jobList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + (i + 1), e);
			}
			if (i == totalPage - 1)	break;
			getNextClick().click();
			Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_4S * 2));
			jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
		}
	}

	private void browseJobList(List<WebElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			job.setTitle(el.getText().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("div[class=job-detail-jobdescription]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("a[class=btn btn-primary btn-lg]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		Elements jobInfo = doc.select("div[class=job-field-template]");
		for (Element el : jobInfo) {
			if (el.text().contains("Locations"))
				job.setLocation(el.text().split(":")[1].trim());
			if (el.text().contains("Career Area"))
				job.setCategory(el.text().split(":")[1].trim());
			if (el.text().contains("Posted Date"))
				job.setPostedDate(parseDate(el.text().split("Date:")[1].trim(), DF1, DF2, DF3, DF4));
			if (el.text().contains("JobType"))
				job.setType(el.text().split(":")[1].trim());
			if (el.text().contains("Req #"))
				job.setReferenceId(el.text().split(":")[1].trim());
		}
		return job;
	}

	private int getTotalPage() {
		String totalJob = driver.findElement(By.xpath("//span[@class='k-pager-info k-label']")).getText().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
	}

	private WebElement getNextClick() {
		return driver.findElement(By.xpath("//a[@class='k-link k-pager-nav']/span[@class='k-icon k-i-arrow-e']"));
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
