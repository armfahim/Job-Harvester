package io.naztech.jobharvestar.scraper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test Comcast Corporation jobs site parsing using selenium web driver.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-10
 */
public class TestComcastCorporationSelenium extends TestAbstractScrapper{

	private static String SITE = "https://comcast.jibeapply.com/jobs?page=1";
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}
	
	@Test
	public void testGetTotalJob() throws InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		Thread.sleep(5000);
		driver.findElement(By.id("pixel-consent-accept-button")).click();
	//	List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-results-container']/search-job-cards/mat-accordion/mat-expansion-panel")));
		System.out.println(driver.findElement(By.id("search-results-indicator")).getText().replace("Results", "").trim());
	}
	
	@Test
	public void testGetJobList() throws InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		Thread.sleep(5000);
		driver.findElement(By.id("pixel-consent-accept-button")).click();
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-results-container']/search-job-cards/mat-accordion/mat-expansion-panel")));
		System.out.println(jobLinks.size());
		SITE = SITE.substring(0, 40);
		System.out.println(SITE);
	}
	
	@Test
	public void testFirstPage() throws InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		Thread.sleep(5000);
		driver.findElement(By.id("pixel-consent-accept-button")).click();
		List<WebElement> jobListE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-results-container']/search-job-cards/mat-accordion/mat-expansion-panel")));
		for (int i = 0; i < jobListE.size(); i++) {
			System.out.println(jobListE.get(i).findElements(By.tagName("a")).get(0).getAttribute("href"));
			WebElement location = jobListE.get(i).findElements(By.className("description-container")).get(0);
			System.out.println(location.findElements(By.tagName("p")).get(0).findElements(By.tagName("span")).get(1).getText());
			WebElement category = jobListE.get(i).findElements(By.className("job-result__category")).get(0);
			System.out.println(category.findElements(By.tagName("span")).get(1).getText());
			WebElement refId = jobListE.get(i).findElements(By.tagName("mat-panel-title")).get(0);
			System.out.println(refId.findElements(By.tagName("span")).get(1).getText());
		}
	}
	
	@Test
	public void testGetNextPage() throws InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		Thread.sleep(5000);
		driver.findElement(By.id("pixel-consent-accept-button")).click();
		List<WebElement> jobListE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-results-container']/search-job-cards/mat-accordion/mat-expansion-panel")));
		for (int i = 0; i < jobListE.size(); i++) {
			System.out.println(jobListE.get(i).findElements(By.tagName("a")).get(0).getAttribute("href"));
			WebElement location = jobListE.get(i).findElements(By.className("description-container")).get(0);
			System.out.println(location.findElements(By.tagName("p")).get(0).findElements(By.tagName("span")).get(1).getText());
		}
		
		driver.get("https://comcast.jibeapply.com/jobs?page="+2);
		jobListE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-results-container']/search-job-cards/mat-accordion/mat-expansion-panel")));
		for (int i = 0; i < jobListE.size(); i++) {
			System.out.println(jobListE.get(i).findElements(By.tagName("a")).get(0).getAttribute("href"));
			WebElement location = jobListE.get(i).findElements(By.className("description-container")).get(0);
			System.out.println(location.findElements(By.tagName("p")).get(0).findElements(By.tagName("span")).get(1).getText());
		}
	}
	
	@Test
	public void testGetJobDetails() throws IOException, InterruptedException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		Thread.sleep(5000);
		driver.findElement(By.id("pixel-consent-accept-button")).click();
		List<WebElement> jobListE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-results-container']/search-job-cards/mat-accordion/mat-expansion-panel")));
		List<String> jobLink = new ArrayList<String>();
		for (int i = 0; i < jobListE.size(); i++) {
			jobLink.add(jobListE.get(i).findElements(By.tagName("a")).get(0).getAttribute("href"));
		}
		
		for(String link:jobLink) {
			driver.get(link);
			try {
				System.out.println(driver.findElement(By.className("job-page-content")).getText());
			}catch(NoSuchElementException e) {
				try {
					System.out.println(driver.findElement(By.xpath("//div[@class='col-md-12']")).getText());
				}catch(NoSuchElementException ex) {
					
				}
				
			}
			
		}
	}

}
