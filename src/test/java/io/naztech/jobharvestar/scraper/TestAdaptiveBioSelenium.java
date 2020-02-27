package io.naztech.jobharvestar.scraper;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Adaptive BioTechnologies job site scraper. <br>
 * URL: https://www.adaptivebiotech.com/about-us/careers/listings/
 * 
 * @author Asadullah Galib
 * @since 2019-03-11
 */
public class TestAdaptiveBioSelenium extends TestAbstractScrapper{

	private static final String SITE = "https://www.adaptivebiotech.com/about-us/careers/listings/";
	private static ChromeDriver driver;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}
	
	@Test
	public void testGetJobList() throws InterruptedException, IllegalMonitorStateException, TimeoutException {
		driver.get(SITE);
		Thread.sleep(TIME_4S);
		driver.switchTo().frame(0);
		List<WebElement> jobLinks = driver.findElements(By.xpath("//section[@class='level-0']/div/a"));
		for (WebElement webElement : jobLinks) {
			System.out.println(webElement.getAttribute("href"));
		}
		

	}
	
	@Test
	public void testGetJobDetail() throws InterruptedException, IllegalMonitorStateException, TimeoutException {
			driver.get(SITE);
	Thread.sleep(TIME_4S);
	driver.switchTo().frame(0);
	List<WebElement> jobLinks = driver.findElements(By.xpath("//section[@class='level-0']/div/a"));
	for (WebElement webElement : jobLinks) {
		System.out.println(webElement.getAttribute("href"));
	}
		System.out.println(jobLinks.size());
		List<String> url = new ArrayList<String>();
		for (WebElement webElement : jobLinks) {
			url.add(webElement.getAttribute("href"));
		}
		
		for (String string : url) {
			driver.get(string);
			driver.switchTo().frame(0);
			System.out.println(driver.findElement(By.id("content")).getText());
		}
	}

}

