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

/**
 *
 * @author tohedul.islum
 * 
 */
public class TestUobThailand {
	private static final String SITE_URL = "https://www.uob.co.th/en/career/listing.html?adobe_mc=MCMID%3D12720723266653794964550431524313994465%7CMCORGID%3D116168F454E6DA2A0A4C98A6%2540AdobeOrg%7CTS%3D1547910731";
	private static ChromeDriverService service;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@Test
	public void getTotalJob() throws InterruptedException {
		driver.get(SITE_URL);
		Thread.sleep(8000);
		wait = new WebDriverWait(driver, 40);
		List<WebElement> list = wait.until(
				ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//ul[@class='dropdown-menu']/li/a"), 0));
		System.out.println(list.size());
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
