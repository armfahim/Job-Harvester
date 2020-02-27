package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.talent.model.Job;

/**
 * Test First Republic Bank jobs site parsing using Selenium.
 * 
 * @author Rahat Ahmad
 * @since 2019-04-18
 */
public class TestUnicreditCRS extends TestAbstractScrapper {

	private static final String SITE = "https://unicreditbank.topjobs.sk/";
	private static ChromeDriver driver;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}

	@Test
	public void testGetJobList()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(SITE);
		List<WebElement> jobListE = getAllJobs();
		for (WebElement webElement : jobListE) {
			System.out.println(webElement.getAttribute("href")); //job url
			System.out.println(webElement.findElement(By.className("jobs-list__title")).getText()); // job title
			System.out.println(webElement.findElements(By.className("jobs-list__column")).get(2).getText()); // job location
			System.out.println(webElement.findElements(By.className("jobs-list__column")).get(3).getText()); // job type
			System.out.println(webElement.findElements(By.className("jobs-list__column")).get(1).getText()); // job category
		}
		System.out.println(jobListE.size());
	}

	public List<WebElement> getAllJobs() throws InterruptedException {
		
		for (;;) {
			if(driver.findElementsByXPath("//a[@class='button jobs__btn js-jobs__more']").size()==0) break;
			driver.findElementsByXPath("//a[@class='button jobs__btn js-jobs__more']").get(0).click();
			Thread.sleep(TIME_1S * 4);
			System.out.println(driver.findElementsByXPath("//a[@class='jobs-list__row']").size());
		}
		return driver.findElementsByXPath("//a[@class='jobs-list__row']");
	}

	@Test
	public void testGetJobDetails() throws IOException, InterruptedException {
		driver.get(SITE);
		List<WebElement> jobListE = getAllJobs();
		List<Job> jobList = new ArrayList<>();
		for (WebElement webElement : jobListE) {
			Job job = new Job();
			job.setUrl(webElement.getAttribute("href")); //job url
			job.setTitle(webElement.findElement(By.className("jobs-list__title")).getText()); // job title
			job.setName(job.getTitle());
			job.setLocation(webElement.findElements(By.className("jobs-list__column")).get(2).getText()); // job location
			job.setType(webElement.findElements(By.className("jobs-list__column")).get(3).getText()); // job type
			job.setCategory(webElement.findElements(By.className("jobs-list__column")).get(1).getText()); // job category
			jobList.add(job);
		}
		for (Job job : jobList) {
			driver.get(job.getUrl());
			System.out.println(driver.findElements(By.xpath("//div[@class='cse-cont cse-detail-wrap']")).get(0).getText()); // spec
		}
		
	}

}
