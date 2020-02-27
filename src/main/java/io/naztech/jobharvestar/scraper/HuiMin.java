package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * huimin job site scrapper.<br> 
 * URL: http://www.huimin.cn/job/index/p/
 * 
 * @author Shadman Shahriar
 * @since 2019-03-24
 */
@Slf4j
@Service
public class HuiMin extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.HUIMIN;
	private static final String HEAD_URL="http://www.huimin.cn/job/index/p/";
	private static final String TAIL_URL=".html";
	private WebDriver driver;
	private WebDriverWait wait;
	private int pageCounter=1;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount = 0;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver=getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(150, TimeUnit.SECONDS);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		getJobDetails(browseJobList(),siteMeta);
	}

	private void nextPage(SiteMetaData siteMeta) throws InterruptedException {
		try {
			List<WebElement> next=wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='fpage']/a")));
			if(next.get(next.size()-1).getText().trim().equals(">")) {
				pageCounter++;
				getJobDetails(browseJobList(),siteMeta);
			} else driver.close();
		} catch (FailingHttpStatusCodeException | ArrayIndexOutOfBoundsException | ElementNotFoundException e) {
			log.warn("Failed to get next page " ,e);
			throw e;
		}
	}
	
	private void getJobDetails(List<WebElement> jobList,SiteMetaData siteMeta) throws InterruptedException {
		expectedJobCount += jobList.size();
		try {
			for (WebElement jobEl : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job=new Job();
				jobEl.click();
				Thread.sleep(TIME_4S);
				try {
				job.setTitle(jobEl.findElement(By.cssSelector(".name")).getText().trim());
				job.setName(job.getTitle());
				job.setCategory(jobEl.findElement(By.cssSelector(".add")).getText().trim());
				job.setPostedDate(parseDate(jobEl.findElement(By.cssSelector(".time")).getText().trim(), DF));
				job.setSpec(jobEl.findElement(By.cssSelector(".employ-info")).getText().trim());
				job.setUrl(getJobHash(job));
				saveJob(job, siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
			nextPage(siteMeta);
		} catch (FailingHttpStatusCodeException | ArrayIndexOutOfBoundsException | ElementNotFoundException e) {
			log.warn("Failed to parse job details " ,e);
		}
	}
	
	private List<WebElement> browseJobList() {
		try {
			driver.get(HEAD_URL+pageCounter+TAIL_URL);
			wait = new WebDriverWait(driver, TIME_1M);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,1000)");
			List<WebElement> jobList=wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//li[@class='str']")));
			return jobList;
		} catch (FailingHttpStatusCodeException | ArrayIndexOutOfBoundsException | ElementNotFoundException e) {
			log.warn("Failed to find element of " + HEAD_URL+pageCounter+TAIL_URL,e);
		}
		return null;
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
