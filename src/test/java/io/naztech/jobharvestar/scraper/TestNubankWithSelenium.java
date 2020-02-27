package io.naztech.jobharvestar.scraper;

import java.io.File;

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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.naztech.talent.model.Job;

/**
 * Nubank job site parsing class.
 * URL: https://nubank.workable.com
 * 
 * @author Shajedul Islam
 * @since 2019-03-12
 */
public class TestNubankWithSelenium extends TestAbstractScrapper {
	private static final String PAGE_URL = "https://nubank.workable.com";
	private static final String TOTAL_JOBS = "//section[@id='jobs']/ul[@class='jobs']/li[@class='job']";
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
	public void testFirstPageJobs() throws InterruptedException 
	{
		driver.get(PAGE_URL);
    	Thread.sleep(5000);
    	
    	List<WebElement> allJobs;
    	List<Job> jobs  = new ArrayList<Job>();
    	List<Job> pushjobs = new ArrayList<Job>();
    	
    	allJobs = driver.findElements(By.xpath("//section[@id='jobs']/ul[@class='jobs']/li[@class='job']"));
    	
    	System.out.println("Jobs Found: "+allJobs.size()+"\n");
    	
    	char ch = '\u00B7';
    	
		for(WebElement webElement : allJobs) {
			
			Job job = new Job();
			
			job.setTitle(webElement.findElement(By.tagName("a")).getText());
			
			String locCat = webElement.findElement(By.tagName("p")).getText();
			
			if(locCat.indexOf('\u00B7') != -1)
			{
				String loc = locCat.split(String.valueOf(ch))[0];
				String cat = locCat.split(String.valueOf(ch))[1];
				
				job.setLocation(loc);
				job.setCategory(cat);
			}
			else
			{
				job.setLocation(locCat);
			}
			
			job.setUrl(webElement.findElement(By.tagName("a")).getAttribute("href"));
			
			jobs.add(job);
		}
		
		Thread.sleep(1000);
		
		for(Job singleJob : jobs)
		{
			driver.get(singleJob.getUrl());
			Thread.sleep(3000);
			
			List<WebElement> jobspechs;
			List <String> spechContainer = new ArrayList<String>();
			
			jobspechs = driver.findElements(By.xpath("//section[@class='section section--text']"));
 
			for(WebElement spech : jobspechs)
			{
				spechContainer.add(spech.getText());
			}
			
			Job jobx = new Job();
			
			jobx.setUrl(singleJob.getUrl());
			jobx.setTitle(singleJob.getTitle());
			jobx.setLocation(singleJob.getLocation());
			jobx.setCategory(singleJob.getCategory());
			
			String speccc = null;
			boolean first = false;
			for(int i=0; i<spechContainer.size(); i++)
			{
				if(first == false)
				{
					speccc = spechContainer.get(i).toString();
					first = true;
				}
				else
				{
					speccc = speccc +"\n"+spechContainer.get(i).toString();
				}
				
			}
			jobx.setSpec(speccc);
			jobx.setApplicationUrl(driver.findElement(By.xpath("//section[@class='section section--cta']/a")).getAttribute("href"));
			
			pushjobs.add(jobx);
		}
		
		int countJobs = 0;
		
		for(Job jobLast : pushjobs)
		{
			System.out.println("Job Url: "+jobLast.getUrl()+"\n");
			System.out.println("Title: "+jobLast.getTitle()+"\n");
			System.out.println("Location: "+jobLast.getLocation()+"\n");
			System.out.println("Category: "+jobLast.getCategory()+"\n");
			System.out.println("JOB SPECH:\n");
			System.out.println(jobLast.getSpec()+"\n");
			System.out.println("Application Url: "+jobLast.getApplicationUrl()+"\n\n");
			countJobs++;
		}
		System.out.println("Job Printed: "+countJobs);
	}

	@Test
	public void testChar() {
		String val = "São Paulo, State of São Paulo, Brazil · Finance";
		System.out.println(val.split("·")[0]);
		char ch = '\u00B7';
		System.out.println(ch);
		System.out.println(val.split(String.valueOf(ch))[0]);
	}
}
