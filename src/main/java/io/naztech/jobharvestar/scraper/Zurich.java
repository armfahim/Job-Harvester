package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Zurich job site parser.<br>
 * URL: https://www.zurich.com/en/careers/search-and-apply<br>
 * 
 * @author fahim.reza
 * @author iftekar.alam
 * @since 2019-03-28
 * 
 */
@Slf4j
@Service
public class Zurich extends AbstractScraper implements Scrapper {
	private final String SITE = ShortName.ZURICH_INSURANCE_GROUP;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM/dd/yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DF4 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	private static final DateTimeFormatter DF5= DateTimeFormatter.ofPattern("dd/MMM/yyyy").withLocale(Locale.ITALIAN);
	private static final DateTimeFormatter DF6= DateTimeFormatter.ofPattern("MMM/dd/yy").withLocale(Locale.FRENCH);
	private String baseUrl;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	public static WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
		driver.get(siteMeta.getUrl());
		Thread.sleep(TIME_10S);
		log.info("Page loading for more jobs, it will take time...");
		for (Job job : getNextButtonClick()) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), siteMeta);					
			} catch(Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}
	
	private Job getJobDetails(Job job) throws InterruptedException {
		driver.get(job.getUrl());
		Thread.sleep(TIME_1S*3);                   
		job.setSpec(driver.findElement(By.xpath("//span[@id='requisitionDescriptionInterface.ID1472.row1']")).getText().trim());
		job.setReferenceId(driver.findElementById("requisitionDescriptionInterface.reqContestNumberValue.row1").getText().trim());
		job.setLocation(driver.findElementById("requisitionDescriptionInterface.ID1534.row1").getText());
		job.setType(driver.findElementById("requisitionDescriptionInterface.ID1646.row1").getText());
		getPostedDate(job);
		return job;
	}
	
	/**
	 * Click for next page and storing jobs url.
	 */
	private List<Job> getNextButtonClick() throws InterruptedException {
		List<Job> rowList = new ArrayList<>();
		int totalNumberToBeClick = getTotalNumberToBeClick();
		WebElement next = driver.findElementsByXPath("//li[@class='c-pagination__item icon icon--arrow c-pagination--control c-pagination--next']").get(1);
		List<WebElement> elJobList = driver.findElementsByXPath("//div[@class='c-results__copy']");
		rowList.addAll(prepareJobLinks(elJobList));
		for (int i = 1; i < totalNumberToBeClick; i++) {
			try {
				next.click();
			} catch (Exception e) {
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", next);
			}
			Thread.sleep(TIME_4S);
			elJobList = driver.findElementsByXPath("//div[@class='c-results__copy']");
			rowList.addAll(prepareJobLinks(elJobList));
		}
		
		return rowList;
	}
	
	/**
	 * Preparing jobs url.
	 */
	private List<Job> prepareJobLinks(List<WebElement> elJobList) throws PageScrapingInterruptedException {
		List<Job> rowList = new ArrayList<>();
		for (WebElement el : elJobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.findElement(By.tagName("a")).getAttribute("href"));
			job.setTitle(el.findElement(By.tagName("a")).getText().trim());
			job.setName(job.getTitle());
			rowList.add(job);
		}
		return rowList;
	}
	
	private int getTotalNumberToBeClick() {
		WebElement elTotalJob = driver.findElementByXPath("//span[@class='c-results__number']");
		String totalJob = elTotalJob.getText().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
	}
	
	/**
	 * Different type of date format exist for this jobs site.
	 * Apply different type of condition for different format
	 */
	private Job getPostedDate(Job job) {
		try {
			String postedDate = driver.findElementById("requisitionDescriptionInterface.reqPostingDate.row1").getText().trim();
			if (postedDate.length() < 9) {
				String[] parts = null;
				if (postedDate.contains("/")) parts = postedDate.split("/");
				if (parts[2].trim().length() == 2) {
					postedDate = parts[0] + "/" + parts[1] + "/20" + parts[2];
					job.setPostedDate(parseDate(postedDate, DF));
				}
			}else if (postedDate.contains(".") && postedDate.contains("/")) {
				if(postedDate.length()==11) postedDate=postedDate.substring(0, 1).toUpperCase()+postedDate.substring(1, 3)+postedDate.substring(4, 11).replace(".", "");
				else if(postedDate.length()==10)postedDate=postedDate.substring(0, 1).toUpperCase()+postedDate.substring(1, 10).replace(".", "");
				if (postedDate.length() < 10) {
					String[] parts = null;
					if (postedDate.contains("/")) parts = postedDate.split("/");
					if (parts[2].trim().length() == 2) {
						postedDate = parts[0] + "/" + parts[1] + "/20" + parts[2];
						job.setPostedDate(parseDate(postedDate, DF2));
					}
				} 
			}else if (postedDate.contains("/") && postedDate.length()==10) job.setPostedDate(parseDate(postedDate, DF3, DF4 , DF6 , DF));
			else if (postedDate.contains("/") && postedDate.length()==11) job.setPostedDate(parseDate(postedDate, DF5));
			else if (postedDate.contains(".") && postedDate.length()==10) job.setPostedDate(parseDate(postedDate, DF1));
		} catch (Exception e) {
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
