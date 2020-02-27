package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TransferWiseTestSelenium  extends TestAbstractScrapper {
	private static final String SITE_URL = "https://www.traveloka.com/en/careers";
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
	public void testFirstPage() throws InterruptedException {
		String Link = "https://www.traveloka.com/en/careers";
		driver.get(Link);
		WebElement search = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@id='searchJob']"))); 
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", search);
		Thread.sleep(TIME_1S);
		List<WebElement> row =  wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='tv-job-list']")));
		wait = new WebDriverWait(driver, 50);	
		System.out.println(row.size());
		System.out.println("Page Url: " + driver.getCurrentUrl());
		for (int i = 0; i < row.size(); i++) {
			System.out.println("url: "+row.get(i).findElement(By.tagName("a")).getAttribute("href"));
			System.out.println("Category: "+row.get(i).findElement(By.className("highlight")).getText());
			System.out.println("Title: "+row.get(i).findElements(By.tagName("a")).get(1).getText());
		}
	}


	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://jobs.smartrecruiters.com/Traveloka/743999681024212--junior-software-engineer";
		driver.get(Link);
		WebElement location = driver.findElementByXPath("//ul[@class='job-details']/li");
		WebElement type = driver.findElementByXPath("//ul[@class='job-details']/li[2]");
		WebElement spec = driver.findElementByXPath("//section[@id='st-jobDescription']");
		WebElement pre = driver.findElementByXPath("//section[@id='st-qualifications']");
		System.out.println(pre.getText());
		System.out.println(spec.getText());
		System.out.println(location.getText());
		System.out.println(type.getText());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}
	


}
