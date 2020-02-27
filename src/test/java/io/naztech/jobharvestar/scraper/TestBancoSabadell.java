package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author Mahmud Rana
 * @since 2019-02-10
 */
public class TestBancoSabadell {

	private static final String DETAIL_PAGE_URL = "https://nasdaq.wd1.myworkdayjobs.com/en-US/Global_External_Site/job/Sweden---Stockholm/Systems-Administrator_R0002991";

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
	public void testJobDetailPage() {
		driver.get(DETAIL_PAGE_URL);
		wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("button[title='Apply']")));
		assertEquals("Junior Systems Administrator with Linux Experience", driver.getTitle());

	}

}
