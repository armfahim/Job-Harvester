package io.naztech.jobharvestar.scraper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Abstract of BambooHR site https://xapo.bamboohr.com/jobs/
 * https://cloudmargin.bamboohr.com/jobs/
 * 
 * @author sohid.ullah
 * @since 2019-04-15
 */

public class TestBamboHRSelenium extends TestAbstractScrapper {

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
		driver.get("https://cloudmargin.bamboohr.com/jobs/");
		List<WebElement> rowList = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='ResAts__listing-link']")));
		System.out.println(rowList.size());
		
		System.out.println(rowList.get(0).getAttribute("href"));
	}

	@Test
	public void testdetailspage() {
		 driver.get("https://cloudmargin.bamboohr.com/jobs/view.php?id=45");
		 
//		 WebElement jobE = wait.until(ExpectedConditions
//					.presenceOfElementLocated(By.xpath("//div[@class='col-xs-12 col-sm-8 col-md-12']")));
//		 
		 
	//	String jobTitle = jobE.findElement(By.tagName("h2")).getText();
//		
//		String categoryAndLocation = jobE.findElement(By.tagName("span")).getText();
//		//String categoryLocationArr[] = categoryAndLocation.split("–");
////		String category = categoryLocationArr[0].trim();
////		String location = categoryLocationArr[1].trim();
//		
//		 WebElement jobSpecE = wait.until(ExpectedConditions
//					.presenceOfElementLocated(By.xpath("//div[@class='ResAts__page ResAts__description js-jobs-page js-jobs-description']")));
		
	//	String spec = jobSpecE.getText();

		
	}
}
