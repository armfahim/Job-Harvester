package io.naztech.jobharvestar.scraper.taleo;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

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
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * AbstractTaleoClick for common taleo.net sites. <br>
 * 
 * Extended Class'es:
 * <ul>
 * <li><a href="https://tas-creditsuisse.taleo.net/careersection/campus/moresearch.ftl?lang=en">Credit Suisse Asst Management</a></li>
 * <li><a href="https://aflac.taleo.net/careersection/external/jobsearch.ftl">Aflac</a></li>
 * <li><a href="https://tas-creditsuisse.taleo.net/careersection/external_advsearch/moresearch.ftl?lang=en#">Credit Suisse</a></li>
 * <li><a href="https://hsbc.taleo.net/careersection/external/moresearch.ftl?lang=en_GB">Hsbs One</a></li>
 * <li><a href="https://citi.taleo.net/careersection/2/jobdetail.ftl">Citi Group</a></li>
 * <li><a href="https://scb.taleo.net/careersection/ex/jobsearch.ftl?lang=en">Standard Chartered</a></li>
 * <li><a href="https://barclays.taleo.net/careersection/2/moresearch.ftl">Barclays</a></li>
 * <li><a href="https://generali.taleo.net/careersection/ex/moresearch.ftl">Generaliltaly</a></li>
 * <li><a href="https://cibc.taleo.net/careersection/1/joblist.ftl">CanadianImperial</a></li>
 * <li><a href="https://hkex.taleo.net/careersection/hkex_hk_external_career_section/joblist.ftl;jsessionid=DdtP_tAsqjcCnhAieU2d0L45lchuz_rj55ETf_s6lQuTUNSKPo2w!992388685">HongKongExchange</a></li>
 * <li><a href="https://principal.taleo.net/careersection/2/joblist.ftl">PrincipalIndia</a></li>
 * <li><a href="https://ms.taleo.net/careersection/2/jobsearch.ftl?lang=en">Morgan Stanley</a></li>
 * </ul>
 * 
 * @author tanmoy.tushar
 * @since 2019-03-31
 */
@Service
public abstract class AbstractTaleoClick extends AbstractScraper implements Scrapper {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private static final DateTimeFormatter DF_1 = DateTimeFormatter.ofPattern("dd-MMM-yyyy, HH:mm:s");
	private static final DateTimeFormatter DF_2 = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
	private static final DateTimeFormatter DF_3 = DateTimeFormatter.ofPattern("d MMM yyyy");
	private static final DateTimeFormatter DF_4 = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final DateTimeFormatter DF_5 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final DateTimeFormatter DF_6 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static final DateTimeFormatter DF_7 = DateTimeFormatter.ofPattern("dd-MMM-yyyy, h:mm:s a");
	private static final DateTimeFormatter DF_8 = DateTimeFormatter.ofPattern("dd/MM/yyyy, h:mm:s a");
	
	private static final String FIRST_JOB_ID = "requisitionListInterface.reqTitleLinkAction.row1";
	private static final String JOB_TITLE_ID = "requisitionDescriptionInterface.reqTitleLinkAction.row1";
	
	protected ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		try {
			driver.get(site.getUrl());
			int totalJob = getTotalJob();
			expectedJobCount = totalJob;
			WebElement firstJobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(FIRST_JOB_ID)));
			try {
				firstJobE.click();				
			} catch (Exception e) {
				driver.navigate().refresh();
				firstJobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(FIRST_JOB_ID)));
				JavascriptExecutor executor = (JavascriptExecutor)driver;
				executor.executeScript("arguments[0].click();", firstJobE);
			}
			Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_1S * 2));
			log.info("Total Job Found: " + totalJob);
			
			browseJobList(site, totalJob);
			
		} catch (TimeoutException e) {
			log.error("Failed to parse job, Site layout changed", e);
			throw e;
		} finally {
			driver.quit();
		}
	}
	
	private int getTotalJob() {
		WebElement totalJob = driver.findElement(By.id(getTotalJobId()));
		return Integer.parseInt(totalJob.getText().trim().split(" ")[2].substring(1));
	}

	private void browseJobList(SiteMetaData site, int totalJob) throws InterruptedException {
		WebElement jobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(JOB_TITLE_ID)));
		for (int i = 0; i < totalJob; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			try {
				job.setTitle(jobE.getText());
				job.setName(job.getTitle());
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				log.warn("Failed to parse job no " + (i + 1), e);
				exception = e;
			}
			if (i == totalJob - 1) break;
			JavascriptExecutor executor = (JavascriptExecutor)driver;
			executor.executeScript("arguments[0].click();", getNextJob());
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_4S));
			jobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(JOB_TITLE_ID)));
		}
	}

	private WebElement getNextJob() {
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(getNextJobId())));
	}

	private Job getJobDetails(Job job) {
		WebElement jobE = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(getSpecId())));
		job.setSpec(jobE.getText().trim());
		try {
			if (getLocationId() != null) {
				jobE = driver.findElement(By.id(getLocationId()));
				job.setLocation(jobE.getText().trim());
			}
			if (getCategoryId() != null) {
				jobE = driver.findElement(By.id(getCategoryId()));
				job.setCategory(jobE.getText().trim());
			}
			if (getJobTypeId() != null) {
				jobE = driver.findElement(By.id(getJobTypeId()));
				job.setType(jobE.getText().trim());
			}
			if (hasRefId()) {
				jobE = driver.findElement(By.id("requisitionDescriptionInterface.reqContestNumberValue.row1"));
				job.setReferenceId(jobE.getText().trim());
			}
			if (hasPostedDate()) {
				jobE = driver.findElement(By.id("requisitionDescriptionInterface.reqPostingDate.row1"));
				job.setPostedDate(parseDate(jobE.getText().trim(), DF_1, DF_2, DF_3, DF_4, DF_5, DF_6, DF_7, DF_8));
			}
			if (getPreReqId() != null) {
				jobE = driver.findElement(By.id(getPreReqId()));
				job.setPrerequisite(jobE.getText().trim());
			}
			
		} catch(NoSuchElementException e) {
			log.info("Some Element isn't available for - " + job.getTitle());
		}
		job.setUrl(getJobHash(job));
		return job;
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

	protected abstract String getTotalJobId();
	protected abstract String getNextJobId();
	protected abstract String getSpecId();
	protected abstract String getPreReqId();
	protected abstract String getLocationId();
	protected abstract String getCategoryId();
	protected abstract String getJobTypeId();
	protected abstract boolean hasRefId();
	protected abstract boolean hasPostedDate();
}
