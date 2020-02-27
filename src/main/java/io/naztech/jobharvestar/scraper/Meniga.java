package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Meniga job site Parser<br>
 * URL:https://jobs.50skills.com/meniga/
 *
 * @author Arifur Rahman
 * @author tanmoy.tushar
 * @since 2019-03-27
 */
@Slf4j
@Service
public class Meniga extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MENIGA;
	private String baseUrl;
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
		wait = new WebDriverWait(driver, 20);
		driver.get(site.getUrl());
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='JobList__JobListContainer-sc-1rox4fq-1 kLTTca']/a")));
		expectedJobCount = jobList.size();
		getSummaryPage(site, jobList);
		driver.quit();
	}

	private void getSummaryPage(SiteMetaData site, List<WebElement> jobList) throws InterruptedException {
		List<Job> urlList = new ArrayList<>();
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			job.setTitle(el.findElement(By.tagName("h2")).getText().trim());
			job.setName(job.getTitle());
			try {
				String[] locType = el.findElement(By.xpath("//div[@class='JobHeader__JobListingSubtitle-sc-17r9u9w-1 ffckwa']")).getText().split("-");
				job.setLocation(locType[0].trim());
				job.setType(locType[1].trim());
			} catch (NoSuchElementException | IndexOutOfBoundsException e) {
				job.setLocation(null);
				job.setLocation(null);
			}
			urlList.add(job);
		}
		for (Job job : urlList) {
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) {
		driver.get(job.getUrl());
		job.setSpec(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='JobDetail__Html-cirha2-2 ljdbvM']"))).getText().trim());
		job.setApplicationUrl(job.getUrl() + "/apply");
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
