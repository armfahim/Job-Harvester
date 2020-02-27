package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Bank of America job site parser<br>
 * URL: http://careers.bankofamerica.com/search-jobs.aspx?c=&r=
 * 
 * @author naym.hossain
 * @author bm.alamin
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-01-29
 */
@Slf4j
@Service
public class BankOfAmerica extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANK_OF_AMERICA;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	public static WebDriverWait wait;
	private String baseUrl;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("M/dd/yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("MM/d/yyyy");
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException, IOException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 50);
		log.info("Page loading for more jobs, it will take time...");
		driver.get(site.getUrl());
		try {
			getAcceptCookies().click();
			Thread.sleep(TIME_4S);
		} catch (Exception e) {
			log.info("No cookies available now");
		}
		expectedJobCount=Integer.parseInt(driver.findElement(By.xpath("//span[@class='results']")).getText().trim());
		viewAllClick();
		try {
			getSummaryPage(site);
		} catch (Exception e) {
			exception = e;
			log.warn("Failed to parse job list of " + site.getUrl(), e);
		}
	}
	
	private void getSummaryPage(SiteMetaData site) throws InterruptedException  {
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-search-tile']")));
		for (int i = 0; i < rowList.size(); i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(rowList.get(i).findElement(By.xpath("div/h3/a")).getAttribute("href"));
			job.setCategory(rowList.get(i).findElements(By.xpath("div/p")).get(1).getText());
			String postDate=getpostDate(rowList.get(i).findElements(By.xpath("div/div/p")).get(0).getText().split("Posted")[1].trim());
			job.setPostedDate(parseDate(postDate,DF1,DF2,DF3,DF));
			try {
				saveJob(getJobDetail(job), site);					
			} catch(Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}
	
	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1").text());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=job-description-body__internal js-job-description-body-internal]").text().trim());
		Element jobE = doc.selectFirst("span[class=js-primary-location]");
		if(jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("p[class=item job-information__type]>span");
		if(jobE != null) job.setType(jobE.text().trim());
		jobE = doc.selectFirst("p[class=item job-information__id]>span");
		if(jobE != null) job.setReferenceId(jobE.text().trim());
		jobE = doc.selectFirst("a[class=apply__cta js-apply__cta t-track-job-apply]");
		if(jobE != null) job.setApplicationUrl(jobE.attr("href"));
		return job;
	}
	
	private void viewAllClick() throws InterruptedException  {
		WebElement nextE= driver.findElement(By.xpath("//a[@class='nav__view-all js-nav__view-all t-track-search-view-all']"));
		try {
			nextE.click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", nextE);
		}
		Thread.sleep(TIME_1M*8);
	}
	
	private String getpostDate(String postedDate) throws InterruptedException {
		if (postedDate.length() < 9) {
			String[] parts = null;
			if (postedDate.contains("/")) parts = postedDate.split("/");
			if (parts[2].trim().length() == 2) {
				postedDate = parts[0] + "/" + parts[1] + "/20" + parts[2];
			}
		}
		return postedDate;
	}
	
	private WebElement getAcceptCookies() {
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='close js-cookie-notification-close t-track-gdpr-close']"))); 
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
