package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
 * Dxc job parsing class<br>
 * URL: https://jobs.dxc.technology/ListJobs
 * 
 * @author mirajul.islam
 * @since 2019-10-21
 */
@Service
@Slf4j
public class DxcTechnology extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DXC;
	private static WebDriver driver;
	private static WebDriverWait wait;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM-dd-yyyy");

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(400, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 100);
		driver.get(site.getUrl());
		this.baseUrl = site.getUrl().substring(0, 27);
		int totalJob = getTotalJob();

		log.info("Preparing Job Links");
		List<WebElement> jobList = driver.findElements(By.xpath("//div[@class='k-grid-content']/table/tbody/tr/td/a"));
		WebElement clickPage = driver.findElement(By.xpath("//a[@class='k-link k-pager-nav'][@title='Next']"));
		for (int i = 0; i < totalJob; i++) {
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
			jobList = driver.findElements(By.xpath("//div[@class='k-grid-content']/table/tbody/tr/td/a"));
			browseJobList(jobList, site);

			clickPage.click();
		}
		log.info("Total Job Link Found: " + jobList.size());
		driver.quit();

	}

	private int getTotalJob() {
		WebElement totalpage = driver.findElement(By.xpath("//span[@class='k-pager-info k-label']"));
		String pageNumber = String.valueOf(totalpage.getText().split("of")[1].trim());
		expectedJobCount = Integer.parseInt(pageNumber);
		return getPageCount(pageNumber, 10);
	}

	public WebElement getShowMoreButton() {
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='k-link k-pager-nav'][@title='Next']")));
	}

	private void browseJobList(List<WebElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (int i = 0; i < jobList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			String url = jobList.get(i).getAttribute("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Job job = new Job(url);
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[id=applyButton-15452]>a");
		job.setApplicationUrl(jobE.attr("href"));
        jobE =doc.getElementById("dnn_ctr15470_ModuleContent");
		job.setReferenceId(jobE.text().trim());	
		jobE = doc.getElementById("dnn_ctr15468_ModuleContent");
		job.setLocation(jobE.text().trim());
		jobE = doc.getElementById("dnn_ctr15469_ModuleContent");
		job.setPostedDate(parseDate(jobE.text(), DF));
		jobE = doc.getElementById("dnn_ctr15466_ContentPane");
		job.setSpec(jobE.text().trim());
		
		return job;
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
