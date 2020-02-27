package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
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
 * Microsoft Corporation job site scrapper of 'Experienced professionals' and 'Students and recent graduates' <br>
 * URL: https://careers.microsoft.com/us/en/search-results?from=0&s=1
 * <ul>
 * <li><a href="https://careers.microsoft.com/us/en/search-results?rt=professional">Experienced professionals</a></li>
 * <li><a href="https://careers.microsoft.com/us/en/search-results?rt=university">Students and recent graduates</a></li>
 * </ul>
 * 
 * @author Tanbirul Hashan
 * @author iftekar.alam
 * @since 2019-03-06
 */
@Slf4j
@Service
public class MicrosoftCorp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MICROSOFT_CORPORATION;
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws TimeoutException, InterruptedException, IOException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(4, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 120);
		driver.get(site.getUrl());
		WebElement totalJob= wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='total-jobs']")));
		expectedJobCount=Integer.parseInt(totalJob.getText());
		for (int i = 0; i <= expectedJobCount; i+=20) {
			String NextUrl=site.getUrl()+i+"&s=1";
			try {
				browseJobList(NextUrl,site);
			} catch (Exception e) {
				log.warn("failed to parse list of "+NextUrl,e);
			}
		}
	}
	
	private void browseJobList(String url, SiteMetaData site) throws IOException, InterruptedException {
		driver.get(url);
		List<Job> jobList = new ArrayList<>();
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='information']/a")));
		for (WebElement el : rowList) {
			Job job=new Job(el.getAttribute("href"));
			jobList.add(job);
		}
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}
	
	
	protected Job getJobDetails(Job job) throws PageScrapingInterruptedException {
		driver.get(job.getUrl());
		List<WebElement> jobElList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@class='lable-text']")));
		List<WebElement> h1EList=driver.findElementsByTagName("h1");
		job.setTitle(h1EList.get(h1EList.size()-1).getText());
		job.setName(job.getTitle());
		job.setReferenceId(jobElList.get(0).getText().trim());
		String date = jobElList.get(1).getText().trim();
		job.setPostedDate(parseDate(date, DF1, DF2));
		job.setCategory(jobElList.get(3).getText().trim());
		job.setType(jobElList.get(5).getText().trim());
		try {
			job.setSpec(driver.findElementByCssSelector("p[data-ph-at-id='job-responsibilities-text']").getText().trim());
			job.setPrerequisite(driver.findElementByCssSelector("p[data-ph-at-id='job-qualifications-text']").getText().trim());
		} catch (NoSuchElementException e1) {
			try {
				job.setSpec(driver.findElementByCssSelector("section[class='job-description']").getText().trim());
			}catch (NoSuchElementException e) {
				job.setSpec(driver.findElementByCssSelector("div[class='job-description']").getText().trim());
			}
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
