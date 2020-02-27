package io.naztech.jobharvestar.scraper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;

/**
 * Truvalue Labs Jobsite Parser url "https://truvalue-labs.breezy.hr/"
 * 
 * @author Muhammad Bin Farook
 * @since 2019-03-25
 */
public class TestTruValue extends TestAbstractScrapper {
	private static final String SITE = "https://truvalue-labs.breezy.hr/";
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
	public void testGetJobList() throws InterruptedException {
		String Link = "https://truvalue-labs.breezy.hr/";
		driver.get(Link);
		wait = new WebDriverWait(driver, 100);

		List<WebElement> jobLinkAnchor = wait.until(
				ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='positions-container']//a"), 0));

		// System.out.println(jobLinks.size());
		List<String> joblink = new ArrayList<String>();
		for (WebElement el : jobLinkAnchor) {

			joblink.add(el.getAttribute("href").toString());

		}

		for (String el : joblink) {

			testGetJobDetails(el);

		}
	}

	public void testGetJobDetails(String link) {

		driver.get(link);
		String title = driver.findElementById("heroBackgroundColor").findElement(By.tagName("h1")).getText();
		System.out.println("Title: " + title);

		String location = driver.findElementByClassName("location").findElement(By.tagName("span")).getText();
		System.out.println("JOB LOCATION " + location);
		String type = driver.findElementByClassName("type").findElement(By.className("polygot")).getText();
		// String prerequisite = driver.findElementByClassName("section").getText();
		System.out.println("TYPE: " + type);

		String URL = driver.findElementByClassName("sidebar-container").findElement(By.tagName("a"))
				.getAttribute("href").toString();

		System.out.println("APPLICATION URL" + URL);
		String description = driver.findElementByClassName("description").getText().trim();
		System.out.println("DESCRIPTON: \n" + description);

		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------------");

		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------------");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}
}
