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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestBancoSantander {
	private static final String DETAIL_PAGE_URL = "https://jobs.santanderbank.com/search-jobs?fl=6252001";
	private static ChromeDriverService service;
	private ChromeDriver driver;
	private WebDriverWait wait;
	@Test
	public void getJobList() throws InterruptedException {
		driver.get(DETAIL_PAGE_URL);
		wait = new WebDriverWait(driver, 30);
		List<WebElement> jobList = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//section[@id = 'search-results-list']/ul/li"), 0));
		for(int i=0;i<jobList.size();i++) {
			System.out.println("Job Link: " + jobList.get(i).findElement(By.tagName("a")).getAttribute("href"));
		}
		List<WebElement> nextPages = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class = 'pagination-paging']/a"), 0));
		nextPages.get(1).click();
		System.out.println("Page 2.....");
		Thread.sleep(5000);
		List<WebElement> jobList2 = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//section[@id = 'search-results-list']/ul/li"), 0));
		for(int i=0;i<jobList.size();i++) {
			System.out.println("Job Link: " + jobList2.get(i).findElement(By.tagName("a")).getAttribute("href"));
		}
	}
	@Test
	public void testDate() {
		String date = "Feb 1, 2019, 10:14:41 AM";
		String[] str = date.split(",");
		String newDate = str[0] + str[1];
		System.out.println(newDate);
	}
	@BeforeClass
	public static void beforeClass() {
		service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
				.usingAnyFreePort().build();
	}

	@Before
	public void beforeTest() {
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(70, TimeUnit.SECONDS);
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
}
