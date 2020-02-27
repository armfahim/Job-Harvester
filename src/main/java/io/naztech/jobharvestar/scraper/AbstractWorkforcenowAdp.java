package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All job site of Workforcenow Adp parsing class. <br>
 * https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=8af61d30-ce97-4d97-af7d-bda5fa023504
 * https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=f533de50-1625-4741-aeef-383de26bed63&ccId=19000101_000001&type=MP&lang=en_CA&selectedMenuKey=CurrentOpenings
 * https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=1a611cd8-4628-463a-89a0-995f0208084d&ccId=19000101_000001&type=MP&lang=en_US
 * https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=c4c744b8-3a1d-428d-8cc5-f90ccbe8d519&ccId=19000101_000001&type=MP&lang=en_US
 * 
 * @author rahat.ahmad
 * @author tanmoy.tushar
 * @since 2019-04-01
 */
@Service
public abstract class AbstractWorkforcenowAdp extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected WebDriverWait wait;
	protected ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		driver.get(site.getUrl());
		int totalJob = getTotalJob();
		browseJobList(totalJob, site);
	}

	private void browseJobList(int totalJob, SiteMetaData site) throws InterruptedException {
		for (int i = 0; i < totalJob; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> rowList = getJobList(totalJob);
			rowList.get(i).click();
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_4S));
			Job job = new Job();
			try {
				job = getJobDetails();
				job.setUrl(getJobHash(job));
				saveJob(job, site);
				getBackButton().click();
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of job no: " + (i + 1), e);
				driver.get(site.getUrl());
			}
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_4S));
		}
	}

	private List<WebElement> getJobList(int totalJob) throws InterruptedException {
		List<WebElement> rowList = driver.findElements(By.xpath("//div[@class='current-openings-actions']/div"));
		if (totalJob > 10 && viewAllJobBtn() != null) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", viewAllJobBtn());
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
		}
		while (rowList.size() < totalJob) {
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_1S * 1));
			rowList = driver.findElements(By.xpath("//div[@class='current-openings-actions']/div"));
		}
		return rowList;
	}

	private Job getJobDetails() throws InterruptedException {
		Job job = new Job();
		job.setSpec(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='job-description-data']"))).getText().trim());
		job.setTitle(driver.findElement(By.className("job-description-title")).getText().trim());
		job.setName(job.getTitle());
		if (getLocation() != null) job.setLocation(getLocation());
		if (getReferenceId() != null) job.setReferenceId(getReferenceId());
		if (getPostedDate() != null) job.setPostedDate(parseAgoDates(getPostedDate()));
		if (getJobType() != null) job.setType(getJobType());
		return job;
	}

	private WebElement viewAllJobBtn() {
		try {
			return wait.until(ExpectedConditions.presenceOfElementLocated(By.id("recruitment_careerCenter_showAllJobs")));
		} catch (TimeoutException e) {
			return null;
		}
	}

	private int getTotalJob() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("vdl-tile__title")));
		String totalJob = el.getText().split(Pattern.quote("("))[1].split(Pattern.quote(")"))[0].trim();
		return expectedJobCount = Integer.parseInt(totalJob);
	}

	private WebElement getBackButton() {
		return driver.findElementById("recruitment_jobDescription_back");
	}

	private String getLocation() {
		try {
			return driver.findElement(By.xpath("//span[@class='job-description-location']/div")).getText().trim();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	private String getReferenceId() {
		try {
			return driver.findElement(By.xpath("//span[@class='job-description-requisition']")).getText().split(":")[1].trim();
		} catch (Exception e) {
			return null;
		}
	}
	
	private String getJobType() {
		try {
			return driver.findElement(By.xpath("//span[@class='job-description-worker-catergory']")).getText().trim();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	private String getPostedDate() {
		try {
			return driver.findElement(By.xpath("//span[@class='job-description-post-date']")).getText().trim();
		} catch (NoSuchElementException e) {
			return null;
		}
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