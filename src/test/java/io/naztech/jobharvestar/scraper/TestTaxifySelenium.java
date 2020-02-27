package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mysql.cj.jdbc.Driver;

/**
 * Taxify website parse <br>
 * URL: https://careers.bolt.eu/positions/
 * 
 * @author sohid.ullah
 * @since 2019-03-19
 */

public class TestTaxifySelenium extends TestAbstractScrapper {

	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testSummaryPage() {
		driver.get("https://careers.bolt.eu/positions");
		List<WebElement> rowList = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//li[@class='my-30']/div/a")));
		System.out.println(rowList.size());
		for (int i = 0; i < rowList.size(); i++) {
			// System.out.println(rowList.get(i).getAttribute("href")); //Link
			System.out.println(rowList.get(i).getText()); // Title
			

		}
	}

	@Test
	public void testdetailspage() {
		driver.get("https://careers.bolt.eu/positions/1A8D97FA2B");
		WebElement jobE = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//div[@class='limit-max-width-1280 pl-5vw pr-5vw mx-auto']")));
		
		WebElement locationEl = driver.findElementByXPath("//div[@class='text-default fw-normal mt-10']");
		String jobTitle = jobE.findElement(By.tagName("h1")).getText();
		
		
		String jobDesc = jobE.getText();
		
		System.out.println(locationEl.getText());
	}
}
