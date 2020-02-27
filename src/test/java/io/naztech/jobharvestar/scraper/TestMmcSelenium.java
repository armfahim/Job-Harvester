package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestMmcSelenium extends TestAbstractScrapper {

	private static String SITE = "https://careers.mmc.com/search-jobs";
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}

	@Test
	public void testGetTotalJob() throws InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		WebElement totalJob = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//section[@id='search-results']/h1")));
		System.out.println(totalJob.getText().replace("SEARCH RESULTS", "").trim());
		driver.findElement(By.id("gdpr-button")).click();
	}

	@Test
	public void testGetJobList() throws InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobListE = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//section[@id='search-results-list']/ul/li")));
		driver.findElement(By.id("gdpr-button")).click();
		System.out.println(jobListE.size());
	}

	@Test
	public void testFirstPage() throws InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobListE = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//section[@id='search-results-list']/ul/li")));
		driver.findElement(By.id("gdpr-button")).click();
		System.out.println(jobListE.size());
		for (int i = 0; i < jobListE.size(); i++) {
			System.out.println(jobListE.get(i).findElement(By.tagName("a")).getAttribute("href"));
			WebElement location = jobListE.get(i).findElements(By.tagName("span")).get(1);
			System.out.println(location.getText());
			WebElement date = jobListE.get(i).findElements(By.tagName("span")).get(2);
			System.out.println(date.getText());
		}
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
		driver.get(SITE);
		Thread.sleep(3000);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobListE = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//section[@id='search-results-list']/ul/li")));
		driver.findElement(By.id("gdpr-button")).click();
		System.out.println(jobListE.size());
		driver.findElement(By.className("next")).click();
		Thread.sleep(5000);
		jobListE = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//section[@id='search-results-list']/ul/li")));
		for (int i = 0; i < jobListE.size(); i++) {
			System.out.println(jobListE.get(i).findElement(By.tagName("a")).getAttribute("href"));
			WebElement location = jobListE.get(i).findElements(By.tagName("span")).get(1);
			System.out.println(location.getText());
			WebElement date = jobListE.get(i).findElements(By.tagName("span")).get(2);
			System.out.println(date.getText());
		}
	}

	@Test
	public void testGetJobDetails() throws IOException, InterruptedException {
		driver.get(SITE);
		Thread.sleep(3000);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> jobListE = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//section[@id='search-results-list']/ul/li")));
		driver.findElement(By.id("gdpr-button")).click();
		System.out.println(jobListE.size());
		List<String> jobLinks = new ArrayList<>();
		for (int i = 0; i < jobListE.size(); i++) {
			jobLinks.add(jobListE.get(i).findElement(By.tagName("a")).getAttribute("href"));
//			WebElement location = jobListE.get(i).findElements(By.tagName("span")).get(1);
//			System.out.println(location.getText());
//			WebElement date = jobListE.get(i).findElements(By.tagName("span")).get(2);
//			System.out.println(date.getText());
		}
		for(String link : jobLinks) {
			driver.get(link);
			System.out.println(driver.findElement(By.className("ats-description")).getText());
		}
		
	}

}
