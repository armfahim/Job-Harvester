package io.naztech.jobharvestar.scraper;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class TestNetflixSelenium {
	
	private static final String DETAIL_PAGE_URL = "https://jobs.netflix.com/search";
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
	public void testGetTotalJob() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(DETAIL_PAGE_URL);
		List<WebElement> el = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='css-17670uj exb5qdx0']/section")));
		System.out.println(el.size());
	}
	
	@Test
	public void testGetJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(DETAIL_PAGE_URL);
		List<WebElement> el = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='css-17670uj exb5qdx0']/section")));
		for(WebElement webE : el) {
			System.out.println(webE.findElement(By.tagName("a")).getAttribute("href"));
			System.out.println(webE.findElement(By.tagName("a")).getText());
			System.out.println(webE.findElements(By.tagName("span")).get(0).getText());
			System.out.println(webE.findElements(By.tagName("span")).get(1).getText());
		}
		
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(DETAIL_PAGE_URL);
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("css-2t20s4")));
		System.out.println(el.getText().replace("of ", "").trim());
	}

}
