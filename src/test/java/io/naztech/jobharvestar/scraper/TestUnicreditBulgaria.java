package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test Amazon jobs site parsing using selenium web driver.
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-06
 */
public class TestUnicreditBulgaria extends TestAbstractScrapper {
	private static final String SITE = "https://careers.unicreditbulbank.bg/?sap-language=DE&sap-wd-configId=/UCIHR/HRRCF_A_UNREG_JOB_SEARCH#";
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 70);
	}

	@Test
	public void testDates() {

	}

	@Test
	public void testGetJobList() {

	}

	@Test
	public void testFirstPage() {

		driver.get(SITE);
		wait.until(presenceOfElementLocated(By.id("pagination")));
		System.out.println(driver.getCurrentUrl());

		List<WebElement> list = driver.findElements(By.className("step"));
		System.out.println("list.size(): " + list.size());
		
		WebElement nextButtonn = list.get(0);
		list.get(0).click();
		wait.until(presenceOfElementLocated(By.id("pagination")));

		int track = 3;
		while (true) {
			System.out.println("track: " + track);
			boolean flag = true;

			list = driver.findElements(By.className("step"));
			System.out.println("list.size(): " + list.size());
			
			for (int i = 1; i < list.size()-1; i++) {
				int pageNumber = Integer.parseInt(list.get(i).getText());
				System.out.println("pageNumber: " + pageNumber + " track: " + track);
				if (pageNumber == track) {
					flag = false;
					track++;
					WebElement nextButton = list.get(i);
					list.get(i).click();
					wait.until(presenceOfElementLocated(By.id("pagination")));
					break;
				}
			}
			if (flag) break;
		}
		System.out.println("Oh Yes...");
	}

	@Test
	public void testGetNextPage() throws InterruptedException {

	}

	@Test
	public void testGetJobDetails() throws IOException {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}

}