package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
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
 * EPAM job site parser. <br>
 * URL: https://www.epam.com/careers/job-listings?recruitingUrl=%2Fcareers%2Fjob-listings%2Fjob&sort=relevance
 * 
 * @author tanmoy.tushar
 * @since 2019-10-21
 */
@Slf4j
@Service
public class Epam extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.EPAM;
	private static final String ROW_LIST = "//a[@class='search-result__item-name']";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		driver.get(site.getUrl());
		int totalJob = getTotalJob();
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
		driver.executeScript("window.scrollBy(0,document.body.scrollHeight)");
		jobList = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROW_LIST), jobList.size()));
		Set<String> urlSet = new HashSet<String>();
		while (true) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
			try {
				urlSet.addAll(jobList.stream().map(it -> it.getAttribute("href")).collect(Collectors.toSet()));
			} catch (StaleElementReferenceException e) {
				log.info("Stale Exception Occured, collecting url stopped...");
			}
			if (jobList.size() > totalJob - 1) break;
			try {
				getViewMoreButton().click();
				Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_1S * 6));
			} catch (ElementNotInteractableException e) {
				getViewMoreButton();
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", getViewMoreButton());
			} catch (TimeoutException e) {
				break;
			}
		}
		log.info("Total Job Link Found: " + urlSet.size());
		browseJobList(urlSet, site);
	}

	private void browseJobList(Set<String> urlList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (String url : urlList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=vacancy_content]").text().trim());
		job.setReferenceId(job.getUrl().substring(46));
		Element jobE = doc.selectFirst("ul[class=recruiting-page__location]");
		if (jobE != null) job.setLocation(jobE.text().trim());
		return job;
	}

	private int getTotalJob() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@class='search-result__heading']")));
		String totalJob = el.getText().trim().split(" ")[2].trim();
		return expectedJobCount = Integer.parseInt(totalJob);
	}

	private WebElement getViewMoreButton() {
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='search-result__view-more']")));
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
