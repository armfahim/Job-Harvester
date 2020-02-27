package io.naztech.jobharvestar.scraper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test Revolut jobs site parsing using htmlunit.
 *  
 * https://www.revolut.com/careers/all
 * @author Rahat Ahmad
 * @since 2019-03-31
 */

public class TestRevolut extends TestAbstractScrapper{

	private static final String SITE = "https://www.revolut.com/careers/all";
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
				.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='styles__StyledFullTitle-sc-1wb623x-3 iOLLFi']/strong")));
		System.out.println(jobLinks.size());
		
		List<WebElement> jobCatLoc = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='styles__StyledFullTitle-sc-1wb623x-3 iOLLFi']/span")));
		System.out.println(jobCatLoc.size());
		
		List<WebElement> jobDetailsBtn = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='styles__StyledQuestionControl-rg66e6-0 UIEid']")));
		System.out.println(jobDetailsBtn.size());
		int j = 0;
		int k = 1;
		for(int i = 0; i < jobLinks.size(); i++) {
			System.out.println(i + ": " + jobLinks.get(i).getText());
			System.out.println("Job Category: " + jobCatLoc.get(j).getText());
			System.out.println("Job Location: " + jobCatLoc.get(k).getText());
			j += 2;
			k += 2;
			
			jobDetailsBtn.get(i).click();
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
			
			List<WebElement> jobAppUrl = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='styles__StyledAnchorButton-sc-1f7dnh3-1 gbHWxD']")));
			System.out.println(jobAppUrl.get(i).getAttribute("href"));
			List<WebElement> jobSpec = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='styles__StyledCareerDescription-sc-1wb623x-8 jNDNTc']")));
			
			System.out.println("====================================");
			System.out.println(jobSpec.get(i).getText());
			System.out.println("====================================");
			System.out.println(driver.getCurrentUrl());
			
		}
		
	}
	
//	@Test
//	public void testGetJobDetail() throws InterruptedException {
//		driver.get(SITE);
//		List<WebElement> jobLinks = wait.until(ExpectedConditions
//				.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='styles__StyledQuestion-rg66e6-2 kCTXUz']")));
//		for (WebElement webElement : jobLinks) {
//			Job job = new Job();
//			job.setTitle(webElement.findElement(By.className("rvl-OpenPositions-title")).getText());
//			System.out.println(job.getTitle());
//			job.setCategory(webElement.findElement(By.className("rvl-OpenPositions-department")).getText());
//			System.out.println(job.getCategory());
//			job.setLocation(webElement.findElement(By.className("rvl-OpenPositions-location")).getText());
//			System.out.println(job.getLocation());
//			webElement.findElement(By.className("styles__StyledQuestionControl-rg66e6-0")).click();
//			Thread.sleep(TIME_1S*2);
//			WebElement spec = webElement.findElement(By.className("rvl-OpenPositions-careerDescription"));
//			job.setSpec(spec.findElements(By.tagName("div")).get(0).getText());
//			System.out.println(job.getSpec());
//			job.setApplicationUrl(webElement.findElement(By.className("styles__StyledAnchorButton-sc-1f7dnh3-1")).getAttribute("href"));
//			System.out.println(job.getApplicationUrl());
//		}
//	}

}
