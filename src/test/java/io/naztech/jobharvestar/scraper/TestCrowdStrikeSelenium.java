package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test CrowdStrike jobs site parsing using selenium.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-13
 */

public class TestCrowdStrikeSelenium extends TestAbstractScrapper{

	private static final String SITE = "https://www.crowdstrike.com/careers/";
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
		driver.switchTo().frame(0);
		List<WebElement> jobLinksE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//article[@class='jv-page-body']/div/table/tbody/tr")));
		System.out.println(jobLinksE.size());
	}

}
