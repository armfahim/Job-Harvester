package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
 * UiPath<br>
 * URL: https://www.uipath.com/company/careers
 * 
 * @author Shadman Shahriar
 * @author tanmoy.tushar
 * @since 2019-03-18
 */
@Service
@Slf4j
public class UiPath extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UIPATH;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver(false);
		driver.manage().timeouts().pageLoadTimeout(3, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		startSiteScrapping(getSiteMetaData(ShortName.UIPATH));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl() + "/jobs");
		List<WebElement> rowList = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='CareersJobsListing-list']/div/h4/a")));
		List<WebElement> paginationList = driver.findElements(By.xpath("//ul[@class='CareersJobsListing-pagination']/li"));
		int totalPage = getTotalPage(paginationList);
		expectedJobCount = totalPage * 25;
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				browseJobList(rowList, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page no: " + (i + 1), e);
			}
			if (i == totalPage - 1)
				break;
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", paginationList.get(paginationList.size() - 1));
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
			rowList = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='CareersJobsListing-list']/div/h4/a")));
			paginationList = driver.findElements(By.xpath("//ul[@class='CareersJobsListing-pagination']/li"));
		}
		driver.quit();
	}

	private void browseJobList(List<WebElement> rowList, SiteMetaData site)
			throws InterruptedException, MalformedURLException, IOException {
		String jobBaseUrl = "https://jobs.lever.co/uipath/";
		for (WebElement webElement : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = jobBaseUrl + webElement.getAttribute("href").split("id=")[1];
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h2");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[class=sort-by-time posting-category medium-category-label]");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("div[class=sort-by-team posting-category medium-category-label]");
		if (jobE != null) job.setCategory(jobE.text());
		jobE = doc.selectFirst("div[class=sort-by-commitment posting-category medium-category-label]");
		if (jobE != null) job.setType(jobE.text());
		Elements jobSpec = doc.select("div[class=section page-centered]");
		job.setSpec(jobSpec.get(0).text().trim());
		if (jobSpec.size() > 1) job.setPrerequisite(jobSpec.get(1).text().trim());
		jobE = doc.selectFirst("a[class=postings-btn template-btn-submit hex-color]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		return job;
	}

	private int getTotalPage(List<WebElement> paginationList) {
		return Integer.parseInt(paginationList.get(paginationList.size() - 2).getAttribute("id"));
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
