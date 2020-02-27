package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test Amazon jobs site parsing using selenium web driver.
 *  
 * @author assaduzzaman.sohan
 * @since 2019-03-06
 */
public class TestAmazonSelenium extends TestAbstractScrapper {
//	private static final String SITE = "https://www.amazon.jobs/en/search?base_query=&loc_query=";
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}

	@Test
	public void testDates() {
		String Link = "https://www.amazon.jobs/en/search?base_query=&loc_query=";
		driver.get(Link);
		List<WebElement> dates = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("posting-date"), 0));

		System.out.println("Page Url: " + driver.getCurrentUrl());
		for (int i = 0; i < dates.size(); i++) {
			System.out.println("Date As String: "+dates.get(i).getText().replace("Posted ", "").trim());
			System.out.println("Date As LocalDate: "+parseDate(dates.get(i).getText().replace("Posted ", "").trim(), DF, DF2));
		}
	}

	@Test
	public void testGetJobList() {
		String Link = "https://www.amazon.jobs/en/search?base_query=&loc_query=";
		driver.get(Link);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 0));

		System.out.println("Page Url: " + driver.getCurrentUrl());
		for (int i = 0; i < jobLinks.size(); i++) {
			System.out.println(jobLinks.get(i).getAttribute("href"));
		}
	}

	@Test
	public void testFirstPage() {
		String Link = "https://www.amazon.jobs/en/search?base_query=&loc_query=";
		driver.get(Link);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 0));
		List<WebElement> dates = driver.findElementsByClassName("posting-date");
		List<WebElement> locationAndId = driver.findElements(By.className("location-and-id"));

		System.out.println("Page Url: " + driver.getCurrentUrl());
		for (int i = 0; i < jobLinks.size(); i++) {
			System.out.println(jobLinks.get(i).getAttribute("href"));
			System.out.println(dates.get(i).getText().replace("Posted ", "").trim());
			System.out.println(locationAndId.get(i).getText());
		}
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
		String Link = "https://www.amazon.jobs/en/search?base_query=&loc_query=";
		driver.get(Link);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 9));
		System.out.println("First Page Url: " + driver.getCurrentUrl());

		
		Thread.sleep(RandomUtils.nextLong(25000, 30000));
		WebElement el = driver.findElement(By.cssSelector(".btn.circle.right"));
		el.click();
		jobLinks = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 9));
		
		System.out.println("Job Links of First Page:");
		for (int i = 0; i < jobLinks.size(); i++) {
			System.out.println(jobLinks.get(i).getAttribute("href"));
		}
		
		Thread.sleep(RandomUtils.nextLong(25000, 30000));
		el = driver.findElement(By.cssSelector(".btn.circle.right"));
		el.click();
		jobLinks = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 9));
		
		System.out.println("Next Page Url: " + driver.getCurrentUrl());
		System.out.println("Job Links of Next Page:");
		for (int i = 0; i < jobLinks.size(); i++) {
			System.out.println(jobLinks.get(i).getAttribute("href"));
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://www.amazon.jobs/en/jobs/SF190021770/hvh-launch-team-member-for-brs1";
		driver.get(Link);
		String title = driver.findElementByClassName("title").getText();
		System.out.println("Title: "+title);
		
		String applicationUrl = driver.findElementById("apply-button").getAttribute("href");
		System.out.println("ApplicationUrl: "+applicationUrl);
		
		String prerequisite = driver.findElementByClassName("section").getText();
		System.out.println("Prerequisite: "+prerequisite);
		
		
		/*
		 * Error: invalid selector: Compound class names not permitted
		 */
		String description = driver.findElementByClassName("section description").getText();
		
		/*
		 * Wrong Answer: Getting the Prerequisite part again
		 */
	//	String description1 = driver.findElement(By.cssSelector(".section.description")).getText();
		System.out.println("Description: "+description);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}
	
}