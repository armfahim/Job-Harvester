package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Kateraa jobs site parse <br>
 * URL: https://katerra.com/en/careers.html
 * 
 * @author tanmoy.tushar
 * @since 2019-03-13
 */
@Service
@Slf4j
public class Katerra extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.KATERRA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private ChromeDriver driver;
	private WebDriverWait wait;
	private static WebClient client;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		driver.get(site.getUrl());
		List<WebElement> jobList = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='filter__item__link']")));
		while (true) {
			WebElement showMore = driver.findElementById("filter-load-more");
			try {
				showMore.click();
			} catch (ElementNotInteractableException e) {
				break;
			}
			Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_4S * 2));
			jobList = wait.until(
					ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='filter__item__link']")));
		}
		expectedJobCount = jobList.size();
		browseJobList(jobList, site);
	}

	private void browseJobList(List<WebElement> jobList, SiteMetaData site) {
		List<WebElement> dateList = driver.findElementsByXPath("//time[@itemprop='datePosted']");
		for (int i = 0; i < jobList.size(); i++) {
			Job job = new Job(jobList.get(i).getAttribute("href"));
			job.setPostedDate(parseDate(dateList.get(i).getAttribute("datetime").substring(0, 10), DF));
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = client.getPage(job.getUrl());
		Document doc = Jsoup.connect(page.getElementById("grnhse_iframe").getAttribute("src")).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1[class=app-title]").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.getElementById("content").text().trim());
		job.setLocation(doc.selectFirst("div[class=location]").text().trim());
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
		driver.close();
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
