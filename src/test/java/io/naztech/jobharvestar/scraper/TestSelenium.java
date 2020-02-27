package io.naztech.jobharvestar.scraper;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestSelenium extends TestAbstractScrapper {
	public static String URL = "https://careers.wework.com/search-results";
	public static ChromeDriver driver; 
	public static WebDriverWait wait;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 10);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.quit();
	}
	
	@Test
	public void test() {
		driver.get(URL);
		WebElement nextE;
		while(true) {
			List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='information']/a")));
			System.out.println(rowList.size());
			nextE = driver.findElement(By.xpath("//a[@aria-label='Next']"));
			if(nextE.getAttribute("href") == null) break;
			driver.get(nextE.getAttribute("href"));
		}
	}
	
}
