package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.naztech.talent.model.Job;

/**
 * Test GSA Capital Partners jobs site parsing using selenium.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-07
 */
public class TestGSACapitalPartnersSelenium extends TestAbstractScrapper {
	private static final String SITE = "https://www.gsacapital.com/careers/#/";
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}
	
	@Test
	public void testGetJobList() {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='_job col-xs-12 col-sm-6 col-md-4 col-lg-3 ng-tns-c5-0 ng-star-inserted']")));
		System.out.println(jobLinks.size());
	}
	
	@Test
	public void testGetJobDetails() throws IOException, InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='_job col-xs-12 col-sm-6 col-md-4 col-lg-3 ng-tns-c5-0 ng-star-inserted']")));
		System.out.println(jobLinks.size());
		List<Job> jobList = new ArrayList<>();
		driver.findElement(By.className("cc-btn")).click();
		for(WebElement webElement : jobLinks) {
			Job job = new Job();
			webElement.findElement(By.tagName("button")).click();
			Thread.sleep(5000);
			job.setUrl(driver.findElement(By.tagName("iframe")).getAttribute("src"));
			driver.switchTo().frame(0);
			job.setTitle(driver.findElement(By.className("app-title")).getText());
			job.setLocation(driver.findElement(By.className("location")).getText());
			job.setSpec(driver.findElement(By.id("content")).getText());
			driver.get(SITE);
			jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='_job col-xs-12 col-sm-6 col-md-4 col-lg-3 ng-tns-c5-0 ng-star-inserted']")));
			Thread.sleep(5000);
			jobList.add(job);
		}
		
		for(Job job : jobList) {
			
			System.out.println(job.getUrl());
			System.out.println(job.getTitle());
			System.out.println(job.getLocation());
			System.out.println(job.getSpec());
		}
		
	}

}
