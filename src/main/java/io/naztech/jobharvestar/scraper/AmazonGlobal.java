package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * AMAZON GLOBAL job site parsing class. <br>
 * URL: https://www.amazon.jobs/en/search?base_query=&loc_query=
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-05
 */
@Slf4j
@Service
public class AmazonGlobal extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AMAZON_GLOBAL;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	private static WebClient CLIENT = null;

	private static ChromeDriver driver;
	private static WebDriverWait wait;

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);

		try {
			driver.get(siteMeta.getUrl());
			wait = new WebDriverWait(driver, 70);
			List<WebElement> jobLinks = wait.until(ExpectedConditions
					.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 0));
			List<WebElement> dates = driver.findElementsByClassName("posting-date");
			List<WebElement> locationAndId = driver.findElements(By.className("location-and-id"));
			
			int totalPage = getTotalPage();
			int click = 0;
			while (click < totalPage - 1) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				click++;
				getSummaryPages(jobLinks, dates, locationAndId, siteMeta);
				WebElement el = driver.findElement(By.cssSelector(".btn.circle.right"));
				el.click();
				jobLinks = wait.until(ExpectedConditions
						.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 0));
				dates = driver.findElementsByClassName("posting-date");
				locationAndId = driver.findElements(By.className("location-and-id"));
			}
		} catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
			log.debug("Failed parse job details " + driver.getCurrentUrl() + "\n" + e);
			throw e;
		}
		driver.quit();
	}
	
	private int getTotalPage() {
		String[] part = driver.findElements(By.xpath("//div[@class='col-sm-6 job-count-info']")).get(0).getText().split("of");
		String totalJob = part[1].replace("jobs", "").trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
	}

	private void getSummaryPages(List<WebElement> jobLinks, List<WebElement> dates, List<WebElement> locationAndId,	SiteMetaData siteMeta) throws InterruptedException {
		for (int i = 0; i < jobLinks.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				Job job = new Job(jobLinks.get(i).getAttribute("href"));
				job.setPostedDate(parseDate(dates.get(i).getText().replace("Posted ", "").trim(), DF, DF2));

				String[] parts = locationAndId.get(i).getText().split("\\|");
				job.setLocation(parts[0].trim());
				job.setReferenceId(parts[1].replace("Job ID:", "").trim());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			} catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
				log.warn("Failed parse job details " + driver.getCurrentUrl() + e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		try {
			CLIENT = getFirefoxClient();
			HtmlPage page = CLIENT.getPage(job.getUrl());
			CLIENT.waitForBackgroundJavaScript(TIME_10S);

			job.setTitle(page.getBody().getOneHtmlElementByAttribute("h1", "class", "title").getAttribute("title"));
			job.setName(job.getTitle());
			job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "section description").asText());
			job.setPrerequisite(page.getBody().getOneHtmlElementByAttribute("div", "class", "section").asText());
			job.setApplicationUrl(
					page.getBody().getOneHtmlElementByAttribute("a", "id", "apply-button").getAttribute("href"));
			return job;
		} catch (ArrayIndexOutOfBoundsException | ElementNotFoundException e) {
			log.debug("Failed parse job details " + job.getUrl() + e);
			return job;
		}
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
