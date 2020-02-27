package io.naztech.jobharvestar.scraper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
 * 17zuoye<br>  
 * URL: https://app.mokahr.com/apply/17zuoye/524#/jobs/?keyword=&_k=roz9fw
 * 
 * @author rafayet.hossain
 * @author bm.alamin
 * @since 2019-03-31
 */
@Service
@Slf4j
public class Zuoye extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ZUOYE;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(500, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 15);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException {
		driver.get(site.getUrl());
		WebElement nextBtn;
		while (true) {
			getSummaryPages(site);
			try {
				nextBtn = driver.findElementByXPath("//li[@class=' rc-pagination-next']");
				if (nextBtn != null)
					nextBtn.click();
				continue;
			} catch (NoSuchElementException e) {
				break;
			}
		}
		driver.quit();
	}
	private void getSummaryPages(SiteMetaData site) {
		try {
			List<WebElement> jobListE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='jobs-2J09M']/a")));
			expectedJobCount += jobListE.size();
			for (int i = 0; i < jobListE.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job();
				WebElement title = jobListE.get(i).findElements(By.xpath("//div[@class='title-1X3Vf']")).get(i);
				WebElement location = jobListE.get(i).findElements(By.xpath("//div[@class='status-3wqaa']/span[3]")).get(i);
				WebElement catagory = jobListE.get(i).findElements(By.xpath("//div[@class='status-3wqaa']/span[2]")).get(i);
				WebElement postDate = jobListE.get(i).findElements(By.xpath("//span[@class='opened-at-3hbqT']")).get(i);
				WebElement jobLink = jobListE.get(i).findElements(By.xpath("//a[@class='link-11ZhH']")).get(i);
				job.setTitle(title.getText());
				job.setName(job.getTitle());
				job.setLocation(location.getText());
				job.setCategory(catagory.getText());
				job.setUrl(jobLink.getAttribute("href"));
				job.setPostedDate(parseDate(postDate.getText().split("：")[1], DF));
				try {
					saveJob(getJobDetails(job), site);					
				} catch (Exception e) {
					exception = e;
				}
				driver.navigate().back();
				jobListE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='jobs-2J09M']/a")));
			}
		} catch (TimeoutException | InterruptedException e) {
			log.info(getSiteName() + " Failed parse job", e);
		}
	}
	private Job getJobDetails(Job job) {
		try {
			driver.get(job.getUrl());
			Thread.sleep(TIME_1S);
			WebElement description = driver.findElements(By.xpath("//div[@class='list-k05pg']")).get(0);
			job.setSpec(description.getText());
			return job;
		} catch (TimeoutException | InterruptedException e) {
			log.info(getSiteName() + " Failed parse job details of " + job.getUrl(), e);
			return null;
		}
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