package io.naztech.jobharvestar.scraper;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.javascript.host.media.webkitMediaStream;

import io.naztech.talent.model.Job;

/**
 * The Hut Group job site parsing class.
 * URL: https://www.thg.com/jobs-search/
 * 
 * @author Shajedul Islam
 * @since 2019-03-1""
 */
public class TestUnityWithSelenium extends TestAbstractScrapper {
	private static final String PAGE_URL = "https://careers.unity.com/find-position";
	private static final String TOTAL_CATEGORIES = "//div[@class='g12 nest department-positions']";
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
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(false));
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
	public void getJobLinksByCategory() throws InterruptedException {
		driver.get(PAGE_URL);
		
		Thread.sleep(5000);
		
		List<String> jobLinks = new ArrayList<>();
		
		try
		{
			int countLink = 0;
			int countCategoy = 0;
			List<WebElement> categories = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(TOTAL_CATEGORIES)));
			//List<WebElement> jobLinkspath = new ArrayList<>();
			for(WebElement category: categories)
			{
				
				List<WebElement> jobLinkspaths = category.findElements(By.tagName("a"));
				
				for(WebElement jobLinkPath : jobLinkspaths)
				{
					jobLinks.add(jobLinkPath.getAttribute("href"));
					countLink++;
					System.out.println("Job link added: "+countLink+"\n");
					
				}
				countCategoy++;
				
			}
			
			System.out.println("Total Categories: "+countCategoy+"\n");
			System.out.println("Total Job links: "+jobLinks.size());
		}
		catch(NoSuchElementException e)
		{
			
		}	
		
	}
	
	@Test
	public void getJobDetails() throws InterruptedException {
		driver.get(PAGE_URL);
		
		Thread.sleep(5000);
		
		List<String> jobLinks = new ArrayList<>();
		
		try
		{
			List<WebElement> categories = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(TOTAL_CATEGORIES)));
			for(WebElement category: categories)
			{
				List<WebElement> jobLinkspaths = category.findElements(By.tagName("a"));
				
				for(WebElement jobLinkPath : jobLinkspaths)
				{
					jobLinks.add(jobLinkPath.getAttribute("href"));
				
				}
			}
			
			for(String link : jobLinks)
			{
				driver.get(link);
				Thread.sleep(2000);
				
				WebElement xpathForLocCat = driver.findElement(By.xpath("//div[@class='g3 pb0 sidebar']"));
				WebElement xpathForTitle = driver.findElement(By.xpath("//div[@class='g8 g-center pb0']"));
				WebElement xpathForSpec = driver.findElement(By.xpath("//div[@class='info']"));
				
				System.out.println("Title: "+xpathForTitle.findElement(By.tagName("h1")).getText()+"\n");
				System.out.println("Location: "+xpathForLocCat.findElements(By.tagName("p")).get(0).getText()+"\n");
				System.out.println("Category: "+xpathForLocCat.findElements(By.tagName("p")).get(1).getText()+"\n");
				System.out.println("Spec: \n"+xpathForSpec.getText()+"\n");
				System.out.println("\n");
			}
			
			
		}
		catch(NoSuchElementException e)
		{
			
		}
		
		
		
		
	}

}
