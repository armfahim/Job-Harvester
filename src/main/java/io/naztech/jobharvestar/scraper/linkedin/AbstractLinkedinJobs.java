package io.naztech.jobharvestar.scraper.linkedin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
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
 * All job site of LinkedIn parsing class
 * 
 * <ul>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=265778&locationId=OTHERS.worldwide&pageNum=0&position=1">GroupeBruxellesLambert</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=5873179&trk=companyTopCard_top-card-button&pageNum=0&position=1">Japan
 * Post Holdings</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?f_C=6658636%2C1059748%2C8109%2C15220893%2C24860%2C68754%2C34906%2C74925%2C2073206%2C26729%2C100218%2C163967&locationId=OTHERS.worldwide&trk=jobs_jserp_pagination_4&start=0&count=25&pageNum=0&position=1">Assicurazioni
 * Generali Global</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/orix-jobs?pageNum=0&position=1">Orix
 * Corp</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=2710452&locationId=OTHERS.worldwide&pageNum=0&position=1">Kinnevik
 * B</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=20179&locationId=OTHERS.worldwide&pageNum=0&position=1">Hang
 * Seng Bank</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=222712&trk=companyTopCard_top-card-button&pageNum=0&position=1">Capula
 * Investment Management</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/glenview-capital-jobs?pageNum=0&position=1">Glenview
 * Capital Management</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/point-state-capital-jobs?pageNum=0&position=1">PointState
 * Capital</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/didi-chuxing-jobs?pageNum=0&position=1">PointState
 * Capital</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=1418447&locationId=OTHERS.worldwide&pageNum=0&position=1">App
 * Direct</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=1181097&locationId=OTHERS.worldwide&pageNum=0&position=1">Apttus</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=58194&locationId=OTHERS.worldwide&pageNum=0&position=1">Automattic</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=10929741&locationId=OTHERS.worldwide&pageNum=0&position=1">BenevolentAi</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?locationId=OTHERS.worldwide&f_C=4836709&keywords=&pageNum=0&position=1">Clover
 * Health</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=13624368&trk=companyTopCard_top-card-button&pageNum=0&position=1">Cambricon</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=2857634&locationId=OTHERS.worldwide&pageNum=0&position=1">Coinbase</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=806783&trk=companyTopCard_top-card-button&pageNum=0&position=1">CureVac</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=2366074%2C2986943%2C2614488%2C3126598%2C3198484%2C2907657%2C1682979%2C2003349%2C6672344&trk=companyTopCard_top-card-button&pageNum=0&position=1">Global
 * Fashion Group</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?keywords=Horizon%20Robotics&locationId=us%3A0&pageNum=0&position=1&f_C=7973570">Horizon
 * Robotics</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=272972&locationId=OTHERS.worldwide&pageNum=0&position=1">In
 * Mobi</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=82914&locationId=OTHERS.worldwide&pageNum=0&position=1">InsideSales.com</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=2657359&locationId=OTHERS.worldwide&pageNum=0&position=1">Iron
 * Source</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=3829760&locationId=OTHERS.worldwide&pageNum=0&position=1">Klook</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=13202476&locationId=OTHERS.worldwide&pageNum=0&position=1">Kuaishou</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search/?f_C=10159884&location=United%20States&pageNum=0&position=1">Letgo</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=3833238%2C14511756%2C60368&trk=companyTopCard_top-card-button&pageNum=0&position=1">NetEase</a></li>
 * <li><a href=
 * "https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=1235&trk=companyTopCard_top-card-button&pageNum=0&position=12&location=Worldwide&currentJobId=1163604253">WELLS
 * FARGO & CO</a></li>
 * </ul>
 * TODO Need to update this abstract class continuously as xpath's element and
 * class name changes. Arise an issue when you find abnormality.
 * 
 * @author benajir.ullah
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @author bm.alamin
 * @since 2019-02-27
 */
@Service
public abstract class AbstractLinkedinJobs extends AbstractScraper implements Scrapper {
	private final String JOB_EL_PATH = "//div[@class = 'topcard__content-left']/";
	protected ChromeDriver driver;
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private WebDriverWait wait;
	private int expectedJobCount;
	//private int expectedCounter = 0;
	private int clickCounter = 0;
	private final int JOB_PER_PAGE = 25;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled())
			log.trace(site.getUrl());
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		log.info("Page loading for more jobs. It will take time...");
		driver.get(siteMeta.getUrl());
		Thread.sleep(TIME_5S);
		/*
		 * Try catch used here because of skip abnormally exited sites where site has
		 * zero job.
		 */
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='no-results__text']")));
			log.warn("Currently no job available in this site " + siteMeta.getUrl());
		} catch (TimeoutException e) {
			expectedJobCount = getTotalJob();
			clickCounter = expectedJobCount/JOB_PER_PAGE;
			for (int i = 0; i < clickCounter; i++) {
				seeMoreJobs();
			}
			List<WebElement> urlElList = driver.findElementsByXPath("//ul[@class = 'jobs-search__results-list']/li/a");
			log.info("Total job Found: "+urlElList.size());
			List<String> pageUrlList = getPageUrlList(urlElList);
			for (String url : pageUrlList) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				browseJobList(siteMeta, url);
			}
		} finally {
			driver.quit();
		}
	}

	private void seeMoreJobs() throws InterruptedException {
		//driver.findElementByXPath("//button[@class = 'see-more-jobs']").click();
		try {
			driver.findElementByXPath("//button[@class = 'see-more-jobs']").click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", driver.findElementByXPath("//button[@class = 'see-more-jobs']"));
		}
		Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
	}

	private int getTotalJob() {
		try {
			String totalJob = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='results-context-header__job-count']"))).getText();
			if (totalJob.contains("+"))
				totalJob = totalJob.replace("+", "");
			if (totalJob.contains(","))
				totalJob = totalJob.replace(",", "");
			return Integer.parseInt(totalJob.trim());
		} catch (Exception e) {
			log.info("No Job Available");
			throw e;
		}
	}

	/** Return all available job summary page's URL's */
	protected List<String> getPageUrlList(List<WebElement> list) throws InterruptedException {
		List<String> pageUrlList = new ArrayList<>();
		for (WebElement row : list) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			pageUrlList.add(row.getAttribute("href"));
		}
		return pageUrlList;
	}

	protected void browseJobList(SiteMetaData site, String url) throws InterruptedException {
		try {
			try {
				driver.get(url);
				saveJob(getJobDetails(url), site);
			} catch (Exception e) {
				exception = e;
			}

		} catch (NullPointerException e) {
			log.warn("Failed to parse on Job list page ", e);
		} catch (StaleElementReferenceException ex) {
			driver.navigate().refresh();
			browseJobList(site, url);
		}
	}

	protected Job getJobDetails(String jobUrl) throws InterruptedException, StaleElementReferenceException {
		if (isStopped())
			throw new PageScrapingInterruptedException();
		try {
			Job job = new Job(jobUrl);
			job.setTitle(driver.findElementByXPath(JOB_EL_PATH + "h1").getText().trim());
			job.setName(job.getTitle());
			job.setLocation(driver.findElementByXPath(JOB_EL_PATH + "h3[1]/span[2]").getText().trim());
			try {
				job.setPostedDate(
						parseAgoDates(driver.findElementByXPath(JOB_EL_PATH + "h3[2]/span").getText().trim()));
			} catch (NoSuchElementException e) {
				job.setPostedDate(
						parseAgoDates(driver.findElementByXPath(JOB_EL_PATH + "h3[3]/span").getText().trim()));
			}
			job.setSpec(driver.findElementByXPath("//section[@class = 'description']/div[1]").getText().trim());
			job.setType(
					driver.findElementByXPath("//section[@class = 'description']/ul[1]/li[2]/span").getText().trim());

			job.setCategory(
					driver.findElementByXPath("//section[@class = 'description']/ul[1]/li[3]/span").getText().trim());
			try {
				WebElement el = driver.findElementByXPath("//div[@class = 'topcard__content-right']/a");
				if (el != null)
					job.setApplicationUrl(el.getAttribute("href"));
			} catch (NoSuchElementException e) {
				return job;
			}
			return job;
		} catch (TimeoutException | IndexOutOfBoundsException | NoSuchElementException e) {
			log.warn("Failed to parse job details of " + jobUrl, e);
			return null;
		}

	}

	@Override
	protected int getExpectedJob() {
//		if (expectedJobCount >= ((expectedCounter * 100) / 80))
//			expectedJobCount = expectedCounter;
//		System.out.println("override expectedJobCount 2: "+ expectedJobCount);
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
