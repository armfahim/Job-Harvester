package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
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
 * Baloise Holding Job Site Parser. <br>
 * URL: https://www.baloise.com/jobs/de/alle-jobangebote.html
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-02-13
 */
@Slf4j
@Service
public class BaloiseHolding extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.BALOISE_HOLDING;
	private static final String VIEW_ALL = "//button[@class='c-cta--selection is-search-loadmore']";
	private String baseUrl;
	private WebDriverWait wait;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 30);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<WebElement> jobListE = getAllJobs(siteMeta.getUrl());
		getSummaryPages(jobListE, siteMeta);
		driver.quit();
	}

	private void getSummaryPages(List<WebElement> jobListE, SiteMetaData siteMeta) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		for (int i = 0; i < jobListE.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(jobListE.get(i).findElement(By.tagName("a")).getAttribute("href"));
			job.setLocation(jobListE.get(i).findElement(By.tagName("p")).getText());
			jobList.add(job);
		}
		expectedJobCount = jobList.size();
		log.info("Total Job Found: " + getExpectedJob());
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		Elements jobDetail = doc.select("div[class=c-rich-text--default]");
		job.setSpec(jobDetail.get(0).text());
		job.setPrerequisite(jobDetail.get(1).text());
		jobE = doc.selectFirst("p[class=cta-teaser__subline]");
		if (jobE != null) job.setReferenceId(jobE.text().split(":")[1].trim());
		jobE = doc.selectFirst("a[class=c-cta--primary is-link ]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		return job;
	}

	private List<WebElement> getAllJobs(String siteUrl) throws InterruptedException {
		driver.get(siteUrl);
		WebElement viewAllButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(VIEW_ALL)));
		for (;;) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			if (viewAllButton.getAttribute("class").equals("c-cta--selection is-search-loadmore is-hidden")) {
				break;
			}
			driver.executeScript("arguments[0].click();", driver.findElement(By.xpath("//div[@class='c-result--jobs']/div[2]/div/div/button")));
		}
		return driver.findElements(By.xpath("//ul[@class='result__list']/li"));
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
