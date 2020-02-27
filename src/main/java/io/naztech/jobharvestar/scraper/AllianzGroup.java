package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;
/**
 * Allianz Group job site parser<br>
 * URL: https://careers.allianz.com/en_EN.html
 * 
 * @author bm.alamin
 * @author tanmoy.tushar
 * @since 2019-03-20
 */
@Slf4j
@Service
public class AllianzGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ALLIANZ;
	private static final String ROW_LIST = "//a[@class = 'c-link c-link--block c-link--block-custom c-job-link']";
	private static final String LOAD_MORE_BTN_PATH = "//a[@class='c-link c-link--block u-text-center']";
	private static final int JOBS_PER_CLICK = 9;
	private String baseUrl;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d.M.yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("d.MM.yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("dd.M.yyyy");

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		driver.get(site.getUrl());
		try {
			getAcceptCookies().click();
			Thread.sleep(TIME_1S);
		} catch (Exception e) {
			log.info("No cookies available now");
		}
		browseJobList(getAllJobList(), site);		
	}
	

	private void browseJobList(List<WebElement> jobList, SiteMetaData site) {
		for (WebElement el : jobList) {
			String url = el.getAttribute("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}
	
	private List<WebElement> getAllJobList() throws InterruptedException{
		int totalJob = getTotalJob();
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
		while(true) {
			try {
				showMoreButton().click();
			} catch (Exception e) {
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", showMoreButton());
			}
			Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_10S * 6));
			log.info("Page loading for more jobs. It will take time...");
			jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
			if (jobList.size() > totalJob - JOBS_PER_CLICK - 1) break;
		}		
		return jobList;
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		Element el = doc.getElementById("tab1Content");
		Element el2 = doc.getElementById("tab2Content");
		if(el != null & el2 != null) {
			job.setSpec(el.text().trim() + el2.text().trim());
		}else if(el != null & el2 == null) {
			job.setSpec(el.text().trim());
		}else {
			job.setSpec(el2.text().trim());
		}
		
		el = doc.getElementById("tab3Content");
		if(el != null)
		job.setPrerequisite(el.text().trim());
		
		Elements jobInfo = doc.select("div[class=c-copy   c-tile__product--title u-padding-top-md  u-text-hyphen-auto]");
		if (jobInfo.size() > 5) {
			job.setReferenceId(jobInfo.get(0).text().trim().split("Code")[1].trim());
			job.setDeadline(parseDate(jobInfo.get(1).getElementsByTag("p").get(1).text().trim(), DF1,DF,DF2,DF3));
			job.setLocation(jobInfo.get(2).text().trim());
			job.setType(jobInfo.get(3).text().trim());
			job.setCategory(jobInfo.get(5).text().trim());
		}
		return job;
	}
	
	private WebElement getAcceptCookies() {
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='optanon-allow-all accept-cookies-button']"))); 
	}
	
	private int getTotalJob() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("resultCount")));
		return expectedJobCount = Integer.parseInt(el.getText()); 
	}
	
	private WebElement showMoreButton() {
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(LOAD_MORE_BTN_PATH)));
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
