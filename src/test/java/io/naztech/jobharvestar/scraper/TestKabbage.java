package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class TestKabbage extends TestAbstractScrapper {
	
	private static final String SITE="https://www.kabbage.com/company/careers/positions";
	private static WebDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver=getChromeDriver();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		driver.get(SITE);
		wait=new WebDriverWait(driver, TIME_10S);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,500)");
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='job-listings desktop-jobs ng-scope']")));
		for (WebElement webElement : jobList) {
			System.out.println("URL: "+webElement.getAttribute("href"));
			System.out.println("TITLE: "+webElement.findElements(By.cssSelector(".column ")).get(0).getText().trim());
			System.out.println("CATEGORY: "+webElement.findElements(By.cssSelector(".column ")).get(1).getText().trim());
			System.out.println("LOCATION: "+webElement.findElements(By.cssSelector(".column ")).get(2).getText().trim());
		}
	}
	
	@Test
	public void testGetJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String Link = "https://www.kabbage.com/company/careers/job/1225581";
		driver.get(Link);
		wait=new WebDriverWait(driver, TIME_10S);
		List<WebElement> apply = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='apply-link']/a")));
		WebElement details = driver.findElement(By.cssSelector(".job-content "));
		System.out.println("APPLY URL: "+apply.get(0).getAttribute("href"));
		System.out.println("DETAILS: "+details.getText().trim());
	}

}
