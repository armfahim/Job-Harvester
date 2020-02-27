package io.naztech.jobharvestar.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestTenXwithSelenium extends TestAbstractScrapper {
	private static final String SITE = "https://careers.ten-x.com/";
	private static ChromeDriverService service;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int totalJob = 0;

	@BeforeClass
	public static void beforeClass() {
		service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe")).usingAnyFreePort().build();
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
	public void totalJobs() {
		driver.get(SITE);	
		List<WebElement> jobLinks = driver.findElements(By.xpath("//div[@class='au-target col-md-3 content-list-item']/a"));
		List<String> links = new ArrayList<String>();
		
		for (WebElement el : jobLinks) {
			links.add(el.getAttribute("href"));
		}

		for (String webElement : links) {
			driver.get(webElement);
			wait = new WebDriverWait(driver, 50);
			List<WebElement> jobCount = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='phs-jobs-list-count au-target']/span")));	
			totalJob = Integer.parseInt(jobCount.get(0).getText()) + totalJob ;						
		}
			System.out.println("Total Job: " + totalJob);
	}

	@Test
	public void firstPage() {	
		String Link = "https://careers.ten-x.com/c/engineering-jobs";
		driver.get(Link);
			List<WebElement> jobTitle = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='information']/a/h4")));	

			for(int i=0; i<jobTitle.size();i++) {	
				System.out.println(jobTitle.get(i).getText());
				List<WebElement> jobLocation = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@class='job-location']")));	
				System.out.println(jobLocation.get(i).getText());
				List<WebElement> jobRefNo = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@class='job-id au-target']")));	
				System.out.println(jobRefNo.get(i).getText());
				List<WebElement> jobPostDate = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@class='job-postdate au-target']")));	
				String Dates[] = jobPostDate.get(i).getText().split(":");
				String Date = Dates[1].trim();
				System.out.println(Date);
				List<WebElement> JobUrl = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='information']/a")));	
				System.out.println(JobUrl.get(i).getAttribute("href"));
			}
		}

	@Test
	public void testJobDetails() {
		String Link = "https://careers.ten-x.com/job/TENXA008G5473/Data-Scientist";
		driver.get(Link);
		List<WebElement> jobSpec = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='jd-info au-target']")));		
		System.out.println("Job Spec: " + jobSpec.get(0).getText());
		
		List<WebElement> JobApp = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-header-actions']/a")));	
		System.out.println("Job Application Link: " + JobApp.get(0).getAttribute("href"));
	}	
}
