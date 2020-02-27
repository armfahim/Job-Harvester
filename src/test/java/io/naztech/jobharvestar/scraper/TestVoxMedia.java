package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
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

public class TestVoxMedia {
	private static final String site_url = "https://www.voxmedia.com/pages/careers-jobs";
	private static ChromeDriverService service;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@BeforeClass
	public static void beforeClass() {
		service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
				.usingAnyFreePort().build();
	}

	@Before
	public void setUp() throws Exception {
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	public void TestTotalJobs() throws InterruptedException {
		driver.get(site_url);
		Thread.sleep(5000);
		List<WebElement> allJobs;
		allJobs = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul[@class='c-jobs__list']/li/a")));
		System.out.println(allJobs.size());

	}

	@Test
	public void TestFetchingJobTitleAndTitle() throws InterruptedException {
		driver.get(site_url);
		Thread.sleep(5000);
		List<WebElement> allJobs;
		allJobs = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//*[@id='jobs-list']/ul[@class='c-jobs__list']/li/a")));
		for (int i = 0; i < allJobs.size(); i++) {

			String title = allJobs.get(i).getText();
			String link = allJobs.get(i).getAttribute("href");
			System.out.println(title);
			System.out.println(link);
		}
	}

}
