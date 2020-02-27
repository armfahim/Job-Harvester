package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;

/**
 * Test LigalZoom jobs site parsing using selenium.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-13
 */

public class TestLegalZoomSelenium extends TestAbstractScrapper{

	private static final String SITE = "https://www.legalzoom.com/careers/all-positions?ccc=Search%20All%20Jobs";
	private static final String HEAD = "https://jobs.jobvite.com";
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}
	
	@Test
	public void testGetJobList() throws InterruptedException {
		driver.get(SITE);
		Thread.sleep(TIME_10S*2);
		driver.switchTo().frame(0);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@class='jv-job-list']/tbody/tr/td/a")));
		System.out.println(jobLinks.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(SITE);
		Thread.sleep(TIME_10S*2);
		driver.switchTo().frame(0);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@class='jv-job-list']/tbody/tr/td/a")));
		for (WebElement webElement : jobLinks) {
			System.out.println(webElement.getAttribute("href"));
		}
	}
	
	@Test
	public void testJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(SITE);
		Thread.sleep(TIME_10S*2);
		driver.switchTo().frame(0);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@class='jv-job-list']/tbody/tr/td/a")));
		List<Job> jobList = new ArrayList<>();
		for (WebElement webElement : jobLinks) {
			Job job = new Job();
			job.setUrl(webElement.getAttribute("href"));
			System.out.println(job.getUrl());
			job.setTitle(webElement.getText());
			System.out.println(job.getTitle());
			jobList.add(job);
		}
		
		for (Job job : jobList) {
			driver.get(job.getUrl());
			Thread.sleep(TIME_10S*2);
			driver.switchTo().frame(0);
			job.setApplicationUrl(driver.findElement(By.xpath("//div[@class='jv-job-detail-top-actions']/a")).getAttribute("href"));
			System.out.println(job.getApplicationUrl());
			job.setSpec(driver.findElement(By.xpath("//div[@class='jv-wrapper']/div[2]")).getText());
			System.out.println(job.getSpec());
		}
	}

}
