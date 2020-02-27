package io.naztech.jobharvestar.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.naztech.talent.model.Job;

/**
 * Test Oscar Health Insurance Co. jobs site parsing using jsoup.
 * 
 * https://www.hioscar.com/careers/search?department=-1&location=-1
 * 
 * @author Rahat Ahmad
 * @since 2019-03-31
 */

public class TestOscarInsurance extends TestAbstractScrapper {

	private static final String SITE = "https://www.hioscar.com/careers/search?department=-1&location=-1";
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
	}

	@Test
	public void testGetJobList() throws InterruptedException {
		driver.get(SITE);
		List<WebElement> jobLinks = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='wrapper-1CY1K9VZX-HaYqmvBZIINp']")));
		System.out.println(jobLinks.size());

	}

	@Test
	public void testGetFirstPage() {
		driver.get(SITE);
		List<WebElement> jobLinks = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='wrapper-1CY1K9VZX-HaYqmvBZIINp']")));

		for (WebElement webElement : jobLinks) {
			System.out.println(webElement.getAttribute("href")); //url
			System.out.println(webElement.findElement(By.className("title-3xxn9l9cgPDM806C_RD_jM")).getText()); //title
			System.out
					.println(webElement.findElements(By.className("infoItem-1YF5nzWwMkULb22EJ6KBlT")).get(0).getText()); //category
			System.out
					.println(webElement.findElements(By.className("infoItem-1YF5nzWwMkULb22EJ6KBlT")).get(2).getText()); //location

		}
	}
	
	@Test
	public void testGetJobDetail() throws InterruptedException {
		driver.get(SITE);
		List<WebElement> jobLinks = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='wrapper-1CY1K9VZX-HaYqmvBZIINp']")));
		List<Job> jobList = new ArrayList<>();
		for (WebElement webElement : jobLinks) {
			Job job = new Job();
			job.setUrl(webElement.getAttribute("href"));
			job.setTitle(webElement.findElement(By.className("title-3xxn9l9cgPDM806C_RD_jM")).getText());
			job.setCategory(webElement.findElements(By.className("infoItem-1YF5nzWwMkULb22EJ6KBlT")).get(0).getText());
			job.setLocation(webElement.findElements(By.className("infoItem-1YF5nzWwMkULb22EJ6KBlT")).get(2).getText());
			jobList.add(job);
		}
		
		for (Job job : jobList) {
			driver.get(job.getUrl());
			WebElement spec = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("careerContent-2MbCMkREtEm1Le5QzVgqfb")));
			job.setSpec(spec.getText());
			System.out.println(job.getSpec());
		}
	}

}
