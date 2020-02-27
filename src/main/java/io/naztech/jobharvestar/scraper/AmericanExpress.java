package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * American Express jobsite scraper.<br>
 * URL: https://jobs.americanexpress.com/jobs?page=1&lang=en
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-02-20
 */
@Service
@Slf4j
public class AmericanExpress extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AMERICAN_EXPRESS;
	private static final String ROW_EL_PATH = "//a[@class='job-title-link']";

	private ChromeDriver driver;
	private WebDriverWait wait;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		this.baseUrl = site.getUrl().substring(0, 32);
		driver.get(site.getUrl());
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_EL_PATH)));
		int totalPage = getTotalPage();
		int curPage = 1;
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/jobs?page=" + (++curPage);
			try {
				browseJobList(rowList, site);
				if(i == totalPage -1) break;
				driver.get(url);
				rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_EL_PATH)));
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + driver.getCurrentUrl(), e);
			}
		}
	}
	
	private void browseJobList(List<WebElement> rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (WebElement row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = row.getAttribute("href");
			try {
				saveJob(getJobDetail(url), site);						
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}		
	}
	
	private Job getJobDetail(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Job job = new Job(url);
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("article[itemprop=description]").text().trim());
		Element jobE = doc.selectFirst("li[itemprop=jobLocation]");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("li[itemprop=occupationalCategory]");
		if (jobE != null) job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("li[itemprop=reqId]");
		if (jobE != null) job.setReferenceId(jobE.text().trim());
		jobE = doc.getElementById("link-apply");
		if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		return job;
	}

	private int getTotalPage() {
		WebElement jobCountEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search-results-indicator")));
		expectedJobCount = Integer.parseInt(jobCountEl.getText().split(" ")[0]);
		return getPageCount(jobCountEl.getText().split(" ")[0], 10);
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
