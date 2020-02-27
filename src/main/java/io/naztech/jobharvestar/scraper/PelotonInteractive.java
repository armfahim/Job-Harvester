package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Peloton Interactive jobs site parse <br>
 * URL: https://www.onepeloton.com/company/careers
 * 
 * @author tanmoy.tushar
 * @since 2019-03-14
 */
@Service
@Slf4j
public class PelotonInteractive extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PELOTON_INTERACTIVE;
	private static final String ROW_LIST_PATH = "//section/div/dl/a";
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount = 0;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 20);
		driver.get(site.getUrl());
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
		for (String url : getELementToUrlList(jobList)) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				driver.get(url);
				String category = url.split("onepeloton.com/careers/")[1].toUpperCase();
				jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST_PATH)));
				browseJobList(getELementToUrlList(jobList), category, site);
			} catch (TimeoutException e) {
				log.warn("No job available for " + url);
			}
		}
	}

	private void browseJobList(List<String> jobList, String category, SiteMetaData site) throws PageScrapingInterruptedException {
		expectedJobCount = jobList.size();
		for (String url : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(url);
			job.setCategory(category);
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.getElementById("content").text().trim());
		job.setLocation(doc.selectFirst("div[class=location]").text().trim());
		job.setApplicationUrl(job.getUrl() + "#app");
		return job;
	}

	private List<String> getELementToUrlList(List<WebElement> jobList) {
		return jobList.stream().map(it -> it.getAttribute("href")).collect(Collectors.toList());
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