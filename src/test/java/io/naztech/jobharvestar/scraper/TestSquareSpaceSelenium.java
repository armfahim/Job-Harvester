package io.naztech.jobharvestar.scraper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class TestSquareSpaceSelenium extends TestAbstractScrapper{
	private final static String SITE = "https://www.squarespace.com/about/careers";
	private static WebDriver driver;
	private static WebDriverWait wait;
	private static ChromeDriverService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
				.usingAnyFreePort().build();
	}
	@Before
	public void beforeTest() {
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(false));
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		driver.manage().window();
		wait = new WebDriverWait(driver, 60);
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
	public void TestGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 60);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,1200)");
		List<WebElement> catListUrl = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='grid-cell']")));
		List<String> urlToJobPage = new ArrayList<String>();
		List<String> categoryList = new ArrayList<String>();
		List<Integer> jobCount = new ArrayList<Integer>();
		for (WebElement webElement : catListUrl) {
			urlToJobPage.add(webElement.getAttribute("href").trim());
			categoryList.add(webElement.findElement(By.className("cell-department")).getText().trim());
			jobCount.add(Integer.parseInt(webElement.findElement(By.className("cell-job-count")).getText().trim()));
		}
		for (int i = urlToJobPage.size()-1; i < urlToJobPage.size(); i++) {
			if (jobCount.get(i) > 0) {
				driver.get(urlToJobPage.get(i));
				wait = new WebDriverWait(driver, 60);
				List<WebElement> jobLinkUrl = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//a[@class='list-item']"),0));
				List<String> jobUrl = new ArrayList<String>();
				List<String> jobTitle = new ArrayList<String>();
				List<String> jobLocation = new ArrayList<String>();
				for (WebElement webElement2 : jobLinkUrl) {
					jobUrl.add(webElement2.getAttribute("href").trim());
					jobTitle.add(webElement2.findElement(By.className("item-title")).getText().trim());
					jobLocation.add(webElement2.findElement(By.className("item-location")).getText().trim());
				}
				getJobDetails(jobUrl,jobTitle,jobLocation,categoryList.get(i));
			}
		}
	}
	
	private void getJobDetails(List<String> url,List<String> title,List<String> location,String category) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		for (int i = 0; i < url.size(); i++) {
			driver.get(url.get(i));
			wait = new WebDriverWait(driver, 40);
			System.out.println("TITLE: "+title.get(i));
			System.out.println("LOCATION: "+location.get(i));
			System.out.println("CATEGORY: "+category);
			//iframe
			driver.switchTo().defaultContent();
			driver.switchTo().frame("grnhse_iframe");
			System.out.println("DESCRIPTION: === "+driver.findElement(By.id("content")).getText().trim());
		}
	}

}
