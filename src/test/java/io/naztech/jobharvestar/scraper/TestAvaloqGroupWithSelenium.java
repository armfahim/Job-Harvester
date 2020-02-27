package io.naztech.jobharvestar.scraper;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Avaloq Group job site parsing class.
 * URL: https://www.avaloq.com/en/open-positions
 * 
 * @author Shajedul Islam
 * @since 2019-03-11
 */

public class TestAvaloqGroupWithSelenium extends TestAbstractScrapper {
	private static final String PAGE_URL = "https://www.avaloq.com/en/open-positions";
	private static final String TOTAL_JOBS = "//div[@class='row avlq-list-item avlq-list-collapsible-item']";
	private static ChromeDriverService service;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@BeforeClass
	public static void beforeClass() {
		service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
				.usingAnyFreePort().build();
	}

	@Before
	public void beforeTest() {
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
	}

	@AfterClass
	public static void afterClass() {
		service.stop();
	}

	@After
	public void afterTest() {
		driver.quit();
	}

	@Test
	public void testFirstPageJobs() throws InterruptedException {
		driver.get(PAGE_URL);
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		//List<Job> jobList = new ArrayList<>();
		
		Thread.sleep(5000);
		
		js.executeScript("window.scrollBy(0,500)");
		
		Thread.sleep(1000);
		
		driver.findElement(By.id("_it_smc_liferay_privacy_web_portlet_PrivacyPortlet_okButton")).click();
		
		//Get data
		
		List<WebElement> jobs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(TOTAL_JOBS)));
		
		for(WebElement webElement : jobs) {
			
			webElement.click();
			Thread.sleep(1000);
			
			System.out.println("Job Title: "+(webElement.findElements(By.tagName("p")).get(0)).getText());
			System.out.println("\n");
			System.out.println("Job Location: "+(webElement.findElements(By.tagName("p")).get(1)).getText());
			System.out.println("\n");
			System.out.println("Job Position: "+(webElement.findElements(By.tagName("p")).get(2)).getText());
			System.out.println("\n");
			System.out.println("JOB SPECH:\n");
			System.out.println((webElement.findElements(By.className("col-xs-12")).get(3)).getText());
			System.out.println("\n");
			System.out.println("Job Link: "+(webElement.findElements(By.tagName("a")).get(1)).getAttribute("href"));
			System.out.println("\n");
			//System.out.println((webElement.findElements(By.tagName("p")).get(4)).getText());
			
			System.out.println("\n\n");
			
			
			webElement.click();
			Thread.sleep(1000);
			
		}
	}
}
