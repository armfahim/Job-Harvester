package io.naztech.jobharvestar.scraper;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test Byju Group jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-11
 */
public class TestByjuSelenium extends TestAbstractScrapper{

	private static final String SITE = "https://byjus.com/careers-at-byjus/";
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}
	
	@Test
	public void testGetJobList() throws InterruptedException {
		try {
			driver.get(SITE);
		}catch(TimeoutException e) {
			
		}
		List<WebElement> jobLinks = driver.findElements(By.xpath("//article[@id='post-947227']/div[@class='container']/div/div/a"));
		for (WebElement webElement : jobLinks) {
			System.out.println(webElement.getAttribute("href"));
		}
		System.out.println(jobLinks.size());
	}
	
	@Test
	public void testGetSummarypage() throws InterruptedException {
		try {
			driver.get(SITE);
		}catch(TimeoutException e) {
			
		}
		List<WebElement> jobLinks = driver.findElements(By.xpath("//article[@id='post-947227']/div[@class='container']/div/div/a"));
		List<String> jobLink = new ArrayList<>();
		for (WebElement webElement : jobLinks) {
			jobLink.add(webElement.getAttribute("href"));
		}
		
		for (String string : jobLink) {
			try {
				driver.get(string);
			}catch(TimeoutException e) {
			}
			try {
				List<WebElement> el = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul[@class='job_listings job-list full']/li/a")));
				for (WebElement element : el) {
					System.out.println(element.getAttribute("href"));
				}
			}catch(NoSuchElementException e) {
				
			}
			
		}
	}
	
	@Test
	public void testGetJobDetails() throws InterruptedException {
		try {
			driver.get(SITE);
		}catch(TimeoutException e) {
			
		}
		List<WebElement> jobLinks = driver.findElements(By.xpath("//article[@id='post-947227']/div[@class='container']/div/div/a"));
		List<String> jobLink = new ArrayList<>();
		for (WebElement webElement : jobLinks) {
			jobLink.add(webElement.getAttribute("href"));
		}
		List<String> job = new ArrayList<>();
		for (String string : jobLink) {
			try {
				driver.get(string);
			}catch(TimeoutException e) {
			}
			try {
				List<WebElement> el = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul[@class='job_listings job-list full']/li/a")));
				for (WebElement element : el) {
					System.out.println(element.findElement(By.tagName("h4")).getText());
					job.add(element.getAttribute("href"));
				}
			}catch(NoSuchElementException e) {
				
			}
			for (String string2 : job) {
				try {
					
					driver.get(string2);
				}catch(TimeoutException e) {
				}
				System.out.println(driver.findElement(By.className("job_description")).getText());
				System.out.println(driver.findElement(By.className("location")).getText());
				System.out.println(driver.findElement(By.className("location")).getText());
				System.out.println(driver.findElement(By.xpath("//div[@class='job-overview']/a")).getAttribute("href"));
			}
			
		}
	}
	
	
	
}
