package io.naztech.jobharvestar.scraper.taleo;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
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
 * Abstract Scrapper for common Taleo sites. <br>
 * 
 * Extended Class'es are:
 * <ul>
 * <li><a href="https://xl.taleo.net/careersection/001xlcatlinexternalcareersection/jobsearch.ftl?lang=en">Xl Group</a></li>
 * <li><a href="https://challenger.taleo.net/careersection/ext/jobsearch.ftl#">Challenger</a></li>
 * <li><a href="https://standardlife.taleo.net/careersection/global+sl+external+career+site+eng/jobsearch.ftl">Standard Life Aberdeen</a></li>
 * <li><a href="https://fti.taleo.net/careersection/2/jobsearch.ftl">Franklin Resources</a></li>
 * <li><a href="https://cinfin.taleo.net/careersection/ex/jobsearch.ftl?lang=en">Cincinnati Financial Corp</a></li>
 * <li><a href="https://svbank.taleo.net/careersection/ex/jobsearch.ftl">Svb Financial Group</a></li>
 * <li><a href="https://thehartford.taleo.net/careersection/2/jobsearch.ftl">Hartford Financial Svcs</a></li>
 * <li><a href="https://santander.taleo.net/careersection/career_esp/jobsearch.ftl?lang=en">Banco Stander</a></li>
 * <li><a href="https://usbank.taleo.net/careersection/10000/jobsearch.ftl?lang=en">Us Bank Crop</a></li>
 * <li><a href="https://ocbc.taleo.net/careersection/ocbc_my_external+1st+subm/jobsearch.ftl?lang=en&%23">Ocbc Malaysia</a></li>
 * <li><a href="https://ocbc.taleo.net/careersection/ocbc_chn_external+1st+submmission/jobsearch.ftl?lang=en">Ocbc China</a></li>
 * <li><a href="https://progressive.taleo.net/careersection/2/jobsearch.ftl?lang=en">Progressive Corp</a></li>
 * <li><a href="https://westpac.taleo.net/careersection/westpacgroup/jobsearch.ftl#">Westpac</a></li>
 * <li><a href="https://bmo.taleo.net/careersection/2/jobsearch.ftl?lang=en_GB&keyword=#">Bank Montreal</a></li>
 * <li><a href="https://ti.taleo.net/careersection/ti_ex_campus/jobsearch.ftl?lang=en&portal=101430233&ignoreSavedQuery#">Texus Instruments</a></li>
 * <li><a href="https://oracle.taleo.net/careersection/2/jobsearch.ftl?lang=en">Oracle Corporation</a></li>
 * <li><a href="https://invesco.taleo.net/careersection/11/jobsearch.ftl">Invesco</a></li>
 * <li><a href="https://acetalent.taleo.net/careersection/ace_external/jobsearch.ftl?lang=en#">Chubb</a></li>
 * <li><a href="https://anzglobal.taleo.net/careersection/anz_aus_ext/jobsearch.ftl?lang=en#">Anz Banking Group</a></li>
 * </ul>
 * 
 * 
 * Refactored AbstractTaleo (On 2019-08-29 by tanmoy.tushar) because AbstractTaleo <br> 
 * abstract class was dependent on sub-scraper classes which isn't our aim to create <br>
 * an abstract class. So, refactored the abstract class to fulfill the meaning of abstract <br>
 * class and now sub-scraper classes will only set Xpath of particular sectors for site <br>
 * parsing. If any default X-path of abstract needed to override then developer can <br>
 * override any method by using @override annotation. If you want to see how was <br>
 * abstract before then go to <a href="https://dac-git/">dac-git</a> and see changes.
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-27 
 */
@Service
public abstract class AbstractTaleo extends AbstractScraper implements Scrapper {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a");
	private static DateTimeFormatter DF4 = DateTimeFormatter.ofPattern("MMM dd, yyyy, h:mm:ss a");
	private static DateTimeFormatter DF5 = DateTimeFormatter.ofPattern("dd-MM-yyyy, h:mm:ss a");
	private static DateTimeFormatter DF6 = DateTimeFormatter.ofPattern("dd/MM/yyyy, h:mm:ss a");
	private static DateTimeFormatter DF7 = DateTimeFormatter.ofPattern("dd-MMM-yyyy, h:mm:ss a");
	private static DateTimeFormatter DF8 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static DateTimeFormatter DF9 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static DateTimeFormatter DF10 = DateTimeFormatter.ofPattern("d-MM-yyyy, h:mm:ss a");
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
		wait = new WebDriverWait(driver, 120);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws InterruptedException {
		driver.get(siteMeta.getUrl());
		List<Job> jobList = new ArrayList<>();
		while (true) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			jobList.addAll(parseJobList());
			try {
				browseNextpage();
			} catch (NoSuchElementException e) {
				break;
			}
		}
		expectedJobCount = jobList.size();
		log.info("Total Valid link found: " + getExpectedJob());
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
		driver.quit();
	}

	/**
	 * browseNextPage method used to browse next page <br>
	 * When aria-disabled='true' it throws {@link NoSuchElementException}
	 * 
	 * @throws NoSuchElementException
	 * @throws InterruptedException
	 */
	private void browseNextpage() throws InterruptedException {
		WebElement nextAnchor = driver.findElementByCssSelector("a[id='next'][aria-disabled='false']");
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", nextAnchor);
		Thread.sleep(TIME_1S * 2);
	}

	private List<Job> parseJobList() throws InterruptedException {
		List<Job> jobList = new ArrayList<>();
		try {
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(getJobListPath())));
		} catch (TimeoutException e) {
			driver.navigate().refresh();
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(getJobListPath())));
		}
		List<WebElement> jobElements = driver.findElementsByXPath(getJobListPath());
		for (WebElement el : jobElements) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				Job job = new Job(el.getAttribute("href"));
				job.setTitle(el.getText());
				job.setName(job.getTitle());
				
				if (hasFirstPagePostedDate()) getFirstPagePostedDate(job);
				if (hasFirstPageDeadline()) getFirstPageDeadline(job);
				
				jobList.add(job);
			} catch (StaleElementReferenceException staleEx) {
				log.info("Stale Exception caught for specific job, job list is loading once again.....");
				return parseJobList();
			}
		}
		return jobList;
	}
	
	protected Job getFirstPagePostedDate(Job job) {
		if (getPostedDateInJobListPage() != null)
			job.setPostedDate(parseDate(getPostedDateInJobListPage(), DF1, DF2,DF8,DF9,DF10));
		return job;
	}
	
	protected Job getFirstPageDeadline(Job job) {
		if (getDeadlineInJobListPage() != null)
		job.setDeadline(parseDate(getDeadlineInJobListPage(), DF1, DF2,DF8,DF9,DF10));
		return job;
	}
	

	protected Job getJobDetails(Job job) {
		driver.get(job.getUrl());
		wait = new WebDriverWait(driver, 15);
		job.setSpec(getSpec());
		try {
			if (getPrerequisiteId() != null) job.setPrerequisite(getPrerequisite());
		} catch (NoSuchElementException e) {
		}
		if (hasPostedDate()) getPostedDate(job);
		if (hasDeadline()) getDeadline(job);
		if (hasRefId())	job.setReferenceId(getReferenceId());
		if (getLocationId() != null) job.setLocation(getLocation());
		if (getCategoryId() != null) job.setCategory(getCategory());
		if (getJobTypeId() != null && job.getType() == null) job.setType(getJobType());
		return job;
	}

	private String getPostedDateInJobListPage() {
		try {
			String date=driver.findElementByXPath("//table[@class='jobsbody']/td[3]/div/div").getText().trim();
			if (date.length() < 9) {
				String[] parts = null;
				if (date.contains("/")) parts = date.split("/");
				if(parts[2].trim().length() == 2) {
				date = parts[0] + "/" + parts[1] + "/20" + parts[2]; 
				}
			}
			System.out.println("postDate : "+date);
			return date;
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	private String getDeadlineInJobListPage() {
		try {
			String date=driver.findElementByXPath("//table[@id='jobs']/tbody/tr/td[3]/div/div/span").getText().trim();
			if (date.length() < 9) {
				String[] parts = null;
				if (date.contains("/")) parts = date.split("/");
				if(parts[2].trim().length() == 2) {
				date = parts[0] + "/" + parts[1] + "/20" + parts[2]; 
				}
			}
			System.out.println("deadLine : "+date);
			return date;
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	private String getSpec() {
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(getSpecId()))).getText().trim();
	}
	
	private String getPrerequisite() {
		return driver.findElementById(getPrerequisiteId()).getText().trim();
	}
	
	private String getJobType() {
		return driver.findElementById(getJobTypeId()).getText().trim();
	}
	
	private String getLocation() {
		return driver.findElementById(getLocationId()).getText().trim();
	}
	
	private String getCategory() {
		return driver.findElementById(getCategoryId()).getText().trim();
	}
	
	private String getReferenceId() {
		return driver.findElementById(getRefId()).getText().trim();
	}
	
	private Job getPostedDate(Job job) {
		String date= driver.findElementById(getPostedDateId()).getText().trim();
		if (date.length() < 9) {
			String[] parts = null;
			if (date.contains("/")) parts = date.split("/");
			if(parts[2].trim().length() == 2) {
			date = parts[0] + "/" + parts[1] + "/20" + parts[2]; 
			}
		}
		job.setPostedDate(parseDate(date,DF1, DF2, DF3, DF4, DF5, DF6, DF7,DF8,DF9,DF10));
		return job;
	}
	
	private Job getDeadline(Job job) {
		String date= driver.findElementById(getDeadlineId()).getText().trim();
		if (date.length() < 9) {
			String[] parts = null;
			if (date.contains("/")) parts = date.split("/");
			if(parts[2].trim().length() == 2) {
			date = parts[0] + "/" + parts[1] + "/20" + parts[2]; 
			}
		}
		job.setDeadline(parseDate(date,DF1, DF2, DF3, DF4, DF5, DF6, DF7,DF8,DF9,DF10));
		return job;
	}

	protected String getJobListPath() {
		return "//div[@class='absolute']//a";
	}

	protected String getRefId() {
		return "requisitionDescriptionInterface.reqContestNumberValue.row1";
	}

	protected String getPostedDateId() {
		
		return "requisitionDescriptionInterface.reqPostingDate.row1";
	}
	
    protected String getDeadlineId() {
		
		return "requisitionDescriptionInterface.reqUnpostingDate.row1";
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

	@Override
	protected String getBaseUrl() {
		return null;
	}	

	protected abstract String getSpecId();
	protected abstract String getPrerequisiteId();
	protected abstract String getLocationId();
	protected abstract String getCategoryId();
	protected abstract String getJobTypeId();
	protected abstract boolean hasPostedDate();
	protected abstract boolean hasDeadline();
	protected abstract boolean hasRefId();
	protected abstract boolean hasFirstPagePostedDate();
	protected abstract boolean hasFirstPageDeadline();
	
}
