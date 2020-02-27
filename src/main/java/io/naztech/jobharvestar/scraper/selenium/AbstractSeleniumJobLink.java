package io.naztech.jobharvestar.scraper.selenium;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
 * Use this abstract for all the classes which need to use web driver for browse Job List. <br>
 * And details page can be parsed with JSoup or HtmlUnit
 * 
 * @author tanmoy.tushar
 * @since 2019-04-24
 */
@Service
public abstract class AbstractSeleniumJobLink extends AbstractScraper implements Scrapper {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected ChromeDriver driver;
	protected WebDriverWait wait;
	protected int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 10);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		try {
			driver.get(site.getUrl());
			List<WebElement> rowList = wait.until(presenceOfAllElementsLocatedBy(By.xpath(getRowListPath())));
			expectedJobCount = rowList.size();
			browseJobList(site, rowList);
		} catch (TimeoutException e) {
			log.info("Failed to load job list", e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	protected void browseJobList(SiteMetaData site, List<WebElement> rowList) throws PageScrapingInterruptedException {
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();

			/* Collecting job url from row list */
			Job job = new Job(rowList.get(i).getAttribute("href"));
			if (getFirstPageCatPath() != null) {
				job.setCategory(driver.findElements(By.xpath(getFirstPageCatPath())).get(i).getText());
			}
			if (getFirstPageLocPath() != null) {
				job.setLocation(driver.findElements(By.xpath(getFirstPageLocPath())).get(i).getText());
			}
			try {
				saveJob(getJobDetail(job), site);				
			} catch(Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	protected Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst(getTitleCssQuery());
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst(getSpecCssQuery());
		job.setSpec(jobE.text());
		if (getLocationCssQuery() != null)
			job.setLocation(getElementText(getLocationCssQuery(), doc));
		if (getCategoryCssQuery() != null)
			job.setCategory(getElementText(getCategoryCssQuery(), doc));
		if (getJobTypeCssQuery() != null)
			job.setType(getElementText(getJobTypeCssQuery(), doc));
		if (getRefCssQuery() != null)
			job.setReferenceId(getElementText(getRefCssQuery(), doc));
		if (getPreReqCssQuery() != null)
			job.setPrerequisite(getElementText(getPreReqCssQuery(), doc));
		if (getPostedDateCssQuery() != null)
			job.setPostedDate(parseDate(getElementText(getPostedDateCssQuery(), doc), getDateFormats()));
		if (getApplyUrlCssQuery() != null) {
			jobE = doc.selectFirst(getApplyUrlCssQuery());
			String appUrl = getBaseUrl() != null ? getBaseUrl() : "";
			appUrl += jobE.attr("href");
			job.setApplicationUrl(appUrl);
		}
		return job;
	}

	private String getElementText(String elementCssQuery, Document doc) {
		Element el = doc.selectFirst(elementCssQuery);
		return el == null ? null : el.text().trim();
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

	/* Always provide rowList path to collect job link */
	protected abstract String getRowListPath();
	protected abstract String getFirstPageCatPath();
	protected abstract String getFirstPageLocPath();
	protected abstract String getTitleCssQuery();
	protected abstract String getLocationCssQuery();
	protected abstract String getCategoryCssQuery();
	protected abstract String getJobTypeCssQuery();
	protected abstract String getRefCssQuery();
	protected abstract String getSpecCssQuery();
	protected abstract String getPreReqCssQuery();
	protected abstract String getPostedDateCssQuery();
	protected abstract String getApplyUrlCssQuery();
	protected abstract DateTimeFormatter[] getDateFormats();
}
