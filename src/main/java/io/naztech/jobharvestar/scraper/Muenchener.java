package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * MUENCHENER RUECKVERSICH Job Site Parser<br>
 * URL: https://munichre-jobs.com/en/MunichRe?filter[company.id]=[1,3,5,9,26,29,4,32,33,38,2,7,6,28,25,26,27,28,30,31]<br>
 * 
 * @author Rahat Ahmad
 * @author fahim.reza
 * @author iftekar.alam
 * @since 2019-03-04
 * 
 */
@Slf4j
@Service
public class Muenchener extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MUENCHENER_RUECKVERSICH;
	private String baseUrl;
	private ChromeDriver driver;
	private int expectedJobCount;
	private Exception exception;
	public static WebDriverWait wait;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
		driver.get(siteMeta.getUrl());
		try {
			browseJobList(siteMeta);
		} catch (Exception e) {
			exception=e;
			log.warn("Failed to parse list of " + siteMeta,e);
		}
	}
	
	private void browseJobList(SiteMetaData site) throws IOException, InterruptedException {
		int totalPage=getTotalPage();
		for (int i = 1; i <= totalPage; i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='btn-box']")));
			int j=1;
			for (WebElement el : rowList) {
				if(isStopped()) throw new PageScrapingInterruptedException();
				List<WebElement> loc = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@class='card-header__job-place']")));
				Job job = new Job(el.findElements(By.tagName("a")).get(1).getAttribute("href"));
				job.setLocation(loc.get(j).getText().trim());
				j++;
				try {
					saveJob(getJobDetails(job), site);
				} catch (Exception e) {
					exception=e;
					log.warn("Failed to parse details of " + job.getUrl(),e);
				}
			}
			getClick();	
		}
	}
	
	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.select("div[class=content]").get(0);
		job.setSpec(jobE.text().trim());
	    try {
	    	jobE = doc.select("a[class=apply]").get(0);
	    	job.setApplicationUrl(jobE.attr("href"));
		} catch (NullPointerException | IndexOutOfBoundsException
				e) {
			jobE = doc.select("span[class=ausblenden]>a").get(0);
			job.setApplicationUrl(jobE.attr("href"));
		}
		return job;
	}
	
	private int getTotalPage() throws InterruptedException {
		String totalJob=wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='jobs__match-title']/h3"))).getText().split("Found")[1].split("matching")[0].trim();
		expectedJobCount=Integer.parseInt(totalJob);
		return getPageCount(totalJob, 20);
	}
	
	private void getClick() throws InterruptedException {
		WebElement nextE = driver.findElement(By.xpath("//li[@class='page-item last']/a"));
		try {
			nextE.click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", nextE);
		}
		Thread.sleep(TIME_4S);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	public void stopIt() {

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
