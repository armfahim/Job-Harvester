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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @name Baloise Holding jobsite pareser
 * @author Rahat Ahmad
 * @since 2019-02-13
 */
public class TestBaloiseHolding {
	private static final String DETAIL_PAGE_URL = "https://www.baloise.com/jobs/de/alle-jobangebote.html";
	//private static final String BASEURL = "https://www.baloise.com";
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
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("main#main-content > div > div > div > div > div > p")));
//		assertEquals("Alle Jobangebote", driver.getTitle());
		System.out.println(driver.findElement(By.xpath("//main[@id=\"main-content\"]/div[1]/div[1]/div[1]/div[1]/div[1]/p[1]")).getText());
		System.out.println(driver.findElement(By.xpath("//main[@id=\"main-content\"]/div[1]/div[2]/div[1]/div[1]/button[1]")).getAttribute("class"));
		//driver.findElement(By.xpath("button[@class='c-cta--selection is-search-loadmore']");
		for(int i =1;;i++) {
			if(driver.findElement(By.xpath("//div[@class='c-result--jobs']/div[2]/div/div/button")).getAttribute("class").equals("c-cta--selection is-search-loadmore is-hidden")) {
				break;
			}
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//div[@class='c-result--jobs']/div[2]/div/div/button")));
			System.out.println(i);
		}
		List<WebElement> jobList = driver.findElements(By.xpath("//main[@id=\"main-content\"]/div[1]/div[1]/div[1]/div[1]/ul[1]/li"));
		System.out.println(jobList.size());
	}

	@Test
	public void testJobDetailPage() {
		driver.get(DETAIL_PAGE_URL);
		wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("main#main-content > div > div > div > div > div > p")));
		for(int i =1;;i++) {
			if(driver.findElement(By.xpath("//div[@class='c-result--jobs']/div[2]/div/div/button")).getAttribute("class").equals("c-cta--selection is-search-loadmore is-hidden")) {
				break;
			}
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", driver.findElement(By.xpath("//div[@class='c-result--jobs']/div[2]/div/div/button")));
			System.out.println(i);
		}
		List<WebElement> jobList = driver.findElements(By.xpath("//ul[@class='result__list']/li"));
		System.out.println(jobList.get(0).findElement(By.tagName("a")).getAttribute("href"));
		driver.get(jobList.get(0).findElement(By.tagName("a")).getAttribute("href"));
		
		System.out.println(driver.findElement(By.xpath("//h1[@class='header-stage-headline']")).getText());
		
	}
	


}
