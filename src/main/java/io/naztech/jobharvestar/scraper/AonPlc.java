package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
 * AON PLC Job site Parser. <br>
 * URL: http://jobs.aon.com
 * 
 * @author mohoshina.akter
 * @since 2019-02-11
 * 
 * @author tanmoy.tushar
 * @since 2019-04-21
 */
@Service
@Slf4j
public class AonPlc extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AON_PLC_A;
	private static final DateTimeFormatter DF_1 = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DF_2 = DateTimeFormatter.ofPattern("M/dd/yyyy");
	private static final DateTimeFormatter DF_3 = DateTimeFormatter.ofPattern("MM/d/yyyy");
	private static final DateTimeFormatter DF_4 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 15);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver.get(site.getUrl());
		List<WebElement> rowList = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//td[@class='job-list-title']/a")));
		while (true) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			expectedJobCount += rowList.size();
			browseJobList(rowList, site);
			try {
				WebElement nextE = driver.findElement(By.xpath("//a[@class ='next paginate_button']"));
				nextE.click();
			} catch (NoSuchElementException e) {
				break;
			}
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
			rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//td[@class='job-list-title']/a")));
		}
		driver.quit();
	}

	private void browseJobList(List<WebElement> rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(rowList.get(i).getAttribute("href"));
			job.setTitle(rowList.get(i).getText());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h3");
			job.setLocation(jobE.text().trim());
			jobE = doc.selectFirst("div[class=job-description-content]");
			job.setSpec(jobE.text().trim());
			try {
				String jobInfo = jobE.text().split("Job number:")[1].trim();
				String[] parts = jobInfo.split("Category:");
				job.setReferenceId(parts[0].trim());
				job.setCategory(parts[1].split("Location:")[0].trim());
				job.setPostedDate(parseDate(jobInfo.split(job.getLocation())[1].trim().split(" ")[0], DF_1, DF_2, DF_3, DF_4));
				jobE = doc.selectFirst("div[class=job-apply-share-buttons]>a");
				job.setApplicationUrl(jobE.attr("href"));
			} catch (DateTimeParseException | NullPointerException | ArrayIndexOutOfBoundsException e) {
				job.setPostedDate(null);
				job.setCategory(null);
				job.setReferenceId(null);
				job.setApplicationUrl(null);
			}
			return job;
		} catch (IOException | NullPointerException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
		}
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