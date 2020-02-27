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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.talent.model.Job;

public class TestNavTechnologiesSelenium {

	private static final String SUMMARY_PAGE_URL = "https://www.nav.com/company/careers/#openings";
												 //"https://www.nav.com/company/careers/#openings"

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


	
	
	
	public List<Job> getJobSummary(){

		List<Job> jobs = new ArrayList<>();

		driver.get("https://www.nav.com/company/careers/#openings");
		List<WebElement> jobSummaries = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\"BambooHR-ATS\"]/div/ul/li/ul/li")));

		for (WebElement webE : jobSummaries) {
			Job j = new Job();
			j.setUrl(webE.findElement(By.tagName("a")).getAttribute("href"));
			j.setTitle(webE.findElement(By.tagName("a")).getText());
			j.setLocation(webE.findElement(By.tagName("span")).getText());
            jobs.add(j);
            //System.out.println(j);
            //getJobDetails(j);
		}

		return jobs;

	}

	
	public Job getJobDetails(Job job) {
		try {
			driver.get(job.getUrl());
		WebElement jobsDetailE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[3]/div[2]/div/div[1]/div[1]")));
			
			//html/body/div[1]/div[4]/div/ul
			//html/body/div[1]/div[4]/div/ul/li[3]/div[2] type
			
			//html/body/div[1]/div[4]/div/ul/li[2]/div[2]  cate
			
			job.setSpec(jobsDetailE.getText());
			System.out.println(job);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return job;
	}
	@Test
	public void getJobDetails() {

		//// *[@id="BambooHR-ATS"]/div/ul/li[1]/ul/li[1]/a
		//// *[@id="BambooHR-ATS"]/div/ul/li[1]/ul/li[2]/a

		//// *[@id="BambooHR-ATS"]/div/ul/li[2]/ul/li[1]/a
		//// *[@id="BambooHR-ATS"]/div/ul/li[1]/ul/li[1]/span
		//// *[@id="BambooHR-ATS"]/div/ul/li[2]/ul/li[1]/span
		//// *[@id="BambooHR-ATS"]/div/ul/li/ul/li/a

		//// *[@id="BambooHR-ATS"]/div/ul/li[2]/ul/li[1]/span
		//// *[@id="BambooHR-ATS"]/div/ul/li[3]/ul/li/span
		//// *[@id="BambooHR-ATS"]/div/ul/li/ul/li/a

		
			driver.get("https://www.nav.com/company/careers/#openings");
			List<WebElement> jobsDetailE = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\"BambooHR-ATS\"]/div/ul/li/ul/li")));

			for (WebElement webElement : jobsDetailE) {
				System.out.println(webElement.findElement(By.tagName("a")).getText());

				System.out.println(webElement.findElement(By.tagName("a")).getAttribute("href"));

				System.out.println(webElement.findElement(By.tagName("span")).getText());

			}

		

	}

	@Test
	public void main()  {

		List<Job> jobs = getJobSummary();

		for (Job job : jobs) {
			 getJobDetails(job);
		}
	}

//	public List<Job> testGetJobList()
//			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
//
//		List<Job> jobs = new ArrayList<>();
//
//		driver.get(SUMMARY_PAGE_URL);
//		List<WebElement> el = wait.until(
//				ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\\\"BambooHR-ATS\\\"]/div/ul/li/ul/li")));
//
//		for (WebElement webE : el) {
//			Job j = new Job();
//			j.setUrl(webE.findElement(By.tagName("a")).getAttribute("href"));
//			j.setTitle(webE.findElement(By.tagName("a")).getText());
//			j.setLocation(webE.findElement(By.tagName("span")).getText());
//
//		}
//
//		return jobs;
//
//	}
}
