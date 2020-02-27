package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

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
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @name Swiss Life Holding jobsite pareser
 * @author Rahat Ahmad
 * @since 2019-02-14
 */

public class TestSwissLife {

	private static final String DETAIL_PAGE_URL = "https://www.swisslife.ch/en/karriere/jobs.html";
	private static final String BASE_URL = "https://swisslife.prospective.ch/index.cfm?sprCd=en&wlgo=1&seq=";
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
	public void testJobTotalJob() {
		driver.get(DETAIL_PAGE_URL);
		wait = new WebDriverWait(driver, 30);
		for (int i = 1; i <= 8; i++) {
			driver.get(BASE_URL + i);
			List<WebElement> jobList = driver.findElementsByXPath("//div[@id='itemlist']/div/div");
			assertEquals(13, jobList.size());
		}
	}

	@Test
	public void testJobDetailPage() throws InterruptedException {
		driver.get(BASE_URL + 1);
		Thread.sleep(5000);
		List<WebElement> jobList = driver.findElementsByXPath("//div[@id='itemlist']/div/div");
		String[] str = jobList.get(1).findElement(By.xpath("//div[@class='table_td funktion']/a"))
				.getAttribute("onclick").split("'");
		assertEquals("http://direktlink.prospective.ch?view=3859201c-afec-4cc3-aded-fd0c38a83bc1", str[1]);
	}
}
