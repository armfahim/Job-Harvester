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
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.javascript.host.media.webkitMediaStream;

import io.naztech.talent.model.Job;

/**
 * The Hut Group job site parsing class.
 * URL: https://www.thg.com/jobs-search/
 * 
 * @author Shajedul Islam
 * @since 2019-03-18
 */
public class TestXiaohongshuWithSelenium extends TestAbstractScrapper {
	private static final String PAGE_URL = "https://xiaohongshu.quip.com/about/jobs#job-listings";
	private static final String TOTAL_CATEGORIES = "//section[@id='job-listings']/div[@class='container']";
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
		
		List<String> jobLinks = new ArrayList<String>();
		
		List<WebElement> jobCategories = new ArrayList<WebElement>();
		
		try
		{
			int countLink = 0;
			int countCategoy = 0;
			WebElement mainPath  = driver.findElement(By.xpath("//section[@id='job-listings']/div[@class='container']"));
			List<WebElement> categories = mainPath.findElements(By.className("row"));
		
			
			
			for(int i = 1; i< categories.size(); i++)
			{
				jobCategories.add(mainPath.findElements(By.className("row")).get(i));
				
				
				countCategoy++;
			}
			
			for(WebElement el: jobCategories)
			{
				//System.out.println(el.findElements(By.tagName("h4")).get(0).getText()+"\n");
				List<WebElement> linkpaths =  (el.findElements(By.tagName("li")));
				
				for(WebElement el2 : linkpaths)
				{
					jobLinks.add(el2.findElement(By.tagName("a")).getAttribute("href"));
					System.out.println(el2.findElement(By.tagName("a")).getAttribute("href")+"\n");
				}
					
					//driver.get(el2.findElement(By.tagName("a")).getAttribute("href"));
					//Thread.sleep(500);
					//driver.get(el2.findElement(By.tagName("a")).getAttribute("href"));
					//driver.navigate().refresh();
		
					
					//Actions action = new Actions(driver);
					//action.moveToElement(el2).build().perform();
					
					//el2.click();
					
					//Thread.sleep(1000);
					
					//driver.switchTo().activeElement();
					//Thread.sleep(1000);
					//action.sendKeys(Keys.ESCAPE).build().perform();
					 
					//System.out.println("close clicked\n");
					//Thread.sleep(2000);
					//driver.switchTo().defaultContent();	
			}
			
			for(String s : jobLinks)
			{
				
				WebDriver webDriver = new ChromeDriver();
				
				String mainWinHander = webDriver.getWindowHandle();

				driver.get(s);
				Thread.sleep(500);
				driver.navigate().refresh();
				Thread.sleep(3000);
				
				
				// code for clicking button to open new window is ommited

				//Now the window opened. So here reture the handle with size = 2
				Set<String> handles = webDriver.getWindowHandles();

				for(String handle : handles)
				{
				    if(!mainWinHander.equals(handle))
				    {
				        // Here will block for ever. No exception and timeout!
				        WebDriver popup = webDriver.switchTo().window(handle);
				        WebElement element = driver.findElement(By.xpath("//div[@class='modal-content col-sm-12 col-md-10 col-md-offset-1']"));
						
						System.out.println(element.findElements(By.tagName("h3")).get(0).getText()+"\n");
				        popup.close();
				    }
				}
				
				
				//Thread.sleep(200);
				
				
				//Actions action = new Actions(driver);
				//action.sendKeys(Keys.ESCAPE).build().perform();
				Thread.sleep(500);
			}
			
			/*int popupxpathCount = 0;
			for(WebElement s : jobLinks)
			{
				Actions action = new Actions(driver);
				action.moveToElement(s).build().perform();
				Thread.sleep(1000);
			
				s.click();
				System.out.println("job clicked\n");
				Thread.sleep(2000);
				
				driver.switchTo().activeElement();
				Thread.sleep(1000);
				
				//
				
				WebElement popUpXpath = driver.findElements(By.xpath("//div[@class='modal-content col-sm-12 col-md-10 col-md-offset-1']")).get(popupxpathCount);
				popupxpathCount++;
				
				System.out.println("Title: "+popUpXpath.findElement(By.tagName("h3")).getText()+"\n");
				
				System.out.println("Spec: "+popUpXpath.getText()+"\n");
				
				System.out.println("Application Url: "+popUpXpath.findElement(By.tagName("a")).getAttribute("href")+"\n");
				
				action.sendKeys(Keys.ESCAPE).build().perform();
 
				System.out.println("close clicked\n");
				Thread.sleep(2000);
				driver.switchTo().defaultContent();
			}*/
			//List<WebElement> jobLinkspath = new ArrayList<>();
			
			
			
			
			//System.out.println("Total Categories: "+countCategoy+"\n");
			//System.out.println("Total Job links: "+jobCategories.size());
		}
		catch(NoSuchElementException e)
		{
			System.out.println("Error Occ\n");
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
