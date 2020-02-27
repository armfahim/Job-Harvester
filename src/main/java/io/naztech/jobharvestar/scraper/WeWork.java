package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * We Work jobs site parse <br>
 * URL: https://careers.wework.com/search-results
 * 
 * @author tanmoy.tushar
 * @since 2019-03-11
 */
@Service
@Slf4j
public class WeWork extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.WEWORK;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static WebClient client;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 15);
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver.get(site.getUrl());
		WebElement nextE;
		while (true) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='information']/a")));
			getSummaryPages(site, rowList);
			nextE = driver.findElement(By.xpath("//a[@aria-label='Next']"));
			if (nextE.getAttribute("href") == null) break;
			driver.get(nextE.getAttribute("href"));
		}
	}

	private void getSummaryPages(SiteMetaData site, List<WebElement> jobRowList) throws PageScrapingInterruptedException {
		expectedJobCount += jobRowList.size();
		for (int i = 0; i < jobRowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(jobRowList.get(i).getAttribute("href"));
			List<WebElement> jobL = jobRowList.get(i).findElements(By.tagName("span"));
			if(jobL.get(0).getText().trim().length() > 2) job.setLocation(jobL.get(0).getText().trim());
			try {
				job.setType(jobL.get(9).getText().split(":")[1].trim());				
			} catch(IndexOutOfBoundsException e) {
				/* Intentionally blank */
			}
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			client.waitForBackgroundJavaScript(TIME_10S);
			
			HtmlElement jobE = page.getBody().getFirstByXPath("//div[@class='job-info au-target']/h1");
			if (jobE != null) {
				job.setTitle(jobE.getTextContent());
				job.setName(job.getTitle());				
			}
			
			jobE = page.getBody().getFirstByXPath("//section[@class='job-description']");
			if (jobE != null) job.setSpec(jobE.getTextContent());
			
			List<HtmlElement> jobInfoL = page.getBody().getByXPath("//div[@class='job-other-info']/span");
			if (jobInfoL.size() > 2) {
				job.setCategory(jobInfoL.get(0).getTextContent());
				job.setReferenceId(jobInfoL.get(1).getTextContent().split(":")[1].trim());
				job.setPostedDate(parseDate(jobInfoL.get(2).getTextContent().split(":")[1], DF));
			}
			
			jobE = page.getFirstByXPath("//div[@class='job-header-actions']/a");
			if (jobE != null) job.setApplicationUrl(jobE.getAttribute("href"));
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info("Failed parse job details of " + job.getUrl(), e);
		}
		return null;
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
