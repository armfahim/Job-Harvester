package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
 * Banco Santander US.<br>
 * URL: https://jobs.santanderbank.com/search-jobs?fl=6252001
 * 
 * @author naym.hossain
 * @author assaduzzaman.sohan
 * @author iftekar.alam
 * @since 2019-02-11
 */
@Slf4j
@Service
public class BancoSantanderUs extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANCO_SANTANDER_US;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a");
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM dd, yyyy, h:mm:ss a");
	private String baseUrl;
	private ChromeDriver driver;
	public static WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 15);
		driver.get(site.getUrl());
		this.baseUrl = site.getUrl().substring(0, 30);
		expectedJobCount = Integer.parseInt(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@role='status']"))).getText().split("Jobs")[0].trim());
		int totalPage = Integer.parseInt(driver.findElement(By.xpath("//span[@class='pagination-total-pages']")).getText().split("of")[1].trim());
		WebElement nextE;
		for (int i = 0; i < totalPage; i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> rowList = driver.findElements(By.xpath("//section[@id='search-results-list']/ul/li/a"));
			for (WebElement el : rowList) {
				if(isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(el.getAttribute("href"));
				job.setLocation(el.findElement(By.tagName("span")).getText().trim());
				try {
					saveJob(getJobDetails(job), site);
				} catch (NullPointerException e) {
					saveJob(getJobDetails1(job), site);
				}catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			}
			if(i==(totalPage-1)) break;
			nextE = driver.findElement(By.xpath("//a[@class='next']"));
			nextE.click();
			Thread.sleep(TIME_4S);
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		job.setSpec(doc.getElementById("requisitionDescriptionInterface.ID1536.row1").text().trim());
		job.setReferenceId(doc.getElementById("requisitionDescriptionInterface.reqContestNumberValue.row1").text().trim());
		job.setType(doc.getElementById("requisitionDescriptionInterface.ID1923.row1").text().trim());
		job.setCategory(doc.getElementById("requisitionDescriptionInterface.ID1703.row1").text().trim());
		job.setPrerequisite(doc.getElementById("requisitionDescriptionInterface.ID1598.row1").text().trim());
		job.setApplicationUrl(doc.selectFirst("a[class=job-apply top]").attr("href"));
		job.setPostedDate(parseDate(doc.getElementById("requisitionDescriptionInterface.reqPostingDate.row1").text().trim(), DF,DF1));
		return job;
	}
	
	private Job getJobDetails1(Job job) throws IOException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Element jobE = doc.selectFirst("h1");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			job.setSpec(doc.selectFirst("div[class=job-overview no-company-desc col-md-9]").text().trim());
			job.setApplicationUrl(doc.selectFirst("a[class=job-apply top]").attr("href"));
		} catch (NullPointerException e) {
			exception = e;
			log.warn("Failed to parse job detail of " + job.getUrl(), e);
		}
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
