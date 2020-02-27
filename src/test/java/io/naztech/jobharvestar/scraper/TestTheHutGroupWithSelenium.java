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

import io.naztech.talent.model.Job;

/**
 * The Hut Group job site parsing class.
 * URL: https://www.thg.com/jobs-search/
 * 
 * @author Shajedul Islam
 * @since 2019-03-13
 */
public class TestTheHutGroupWithSelenium extends TestAbstractScrapper {
	private static final String PAGE_URL = "https://www.thg.com/jobs-search";
	private static final String TOTAL_JOBS = "//div[@class='careers__workablejobs']/a[@class='jobLink']";
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
	public void testAllJobs() throws InterruptedException {
		driver.get(PAGE_URL);
		
		Thread.sleep(5000);
		
		List<String> jobLinks = new ArrayList<>();
		
		
		List<WebElement> jobs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(TOTAL_JOBS)));
		
		char ch = '\u00B7';
		
		for(WebElement jobpath : jobs)
		{
			jobLinks.add(jobpath.getAttribute("href"));
		}
		
		int count = 1;
		
		for(String joblink : jobLinks)
		{
			driver.get(joblink);
			Thread.sleep(2000);
			
			List<WebElement> jobspecs;
			List <String> specContainer = new ArrayList<String>();
			
			try 
			{
				
				WebElement head = driver.findElement(By.xpath("//section[@class='section section--header']"));
				jobspecs = driver.findElements(By.xpath("//section[@class='section section--text']"));
				
				for(WebElement spech : jobspecs)
				{
					specContainer.add(spech.getText());
				}
				
				String specString = null;
				boolean first = false;
				for(int i=0; i<specContainer.size(); i++)
				{
					if(first == false)
					{
						specString = specContainer.get(i).toString();
						first = true;
					}
					else
					{
						specString = specString +"\n"+specContainer.get(i).toString();
					}
					
				}
				
				System.out.println(head.findElement(By.tagName("h1")).getText()+"\n");
				
				
				
				String locCat = head.findElements(By.tagName("p")).get(1).getText();
				
				if(locCat.indexOf('\u00B7') != -1)
				{
					String loc = locCat.split(String.valueOf(ch))[0];
					String cat = locCat.split(String.valueOf(ch))[1];
					System.out.println("Location: "+loc+"\n");
					
					System.out.println("Category: "+cat+"\n");
				}
				else
				{
					System.out.println("Location: "+locCat+"\n");
				}
				
				System.out.println("SPEC: \n"+specString+"\n");
				
				System.out.println("Application URL: "+driver.findElement(By.xpath("//section[@class='section section--cta']/a")).getAttribute("href"));
				
				System.out.println("Job URL: "+joblink+"\n");
				
				System.out.println("Jobs Printed: "+count+"\n");
				count++;
				
				System.out.println("\n");
					
			}
			catch(NoSuchElementException e)
			{
				System.out.println("Found a job unavailable !\n");
				System.out.println("\n");
			}
			
			Thread.sleep(1000);
		}
		
	}

}
