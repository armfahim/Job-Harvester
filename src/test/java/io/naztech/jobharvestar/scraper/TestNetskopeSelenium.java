package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test Netskope jobs site parsing using selenium.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-11
 */
public class TestNetskopeSelenium extends TestAbstractScrapper{

	private static final String SITE = "https://www.netskope.com/company/careers/open-positions";
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
		driver.findElement(By.id("CybotCookiebotDialogBodyLevelButtonAccept")).click();
		Thread.sleep(TIME_4S);
		driver.switchTo().frame(0);
		List<WebElement> jobLinks = driver.findElements(By.xpath("//section[@class='level-0']/div/a"));
		List<WebElement> jobLinksOther = driver.findElements(By.xpath("//section[@class='level-0']/section/div/a"));
		for (WebElement webElement : jobLinks) {
			System.out.println(webElement.getAttribute("href"));
		}
		
		for (WebElement webElement : jobLinksOther) {
			System.out.println(webElement.getAttribute("href"));
		}
		System.out.println(jobLinks.size());
		System.out.println(jobLinksOther.size());
	}
	
	@Test
	public void testGetJobDetail() throws InterruptedException {
		driver.get(SITE);
		driver.findElement(By.id("CybotCookiebotDialogBodyLevelButtonAccept")).click();
		Thread.sleep(TIME_4S);
		driver.switchTo().frame(0);
		List<WebElement> jobLinks = driver.findElements(By.xpath("//section[@class='level-0']/div/a"));
		List<WebElement> jobLinksOther = driver.findElements(By.xpath("//section[@class='level-0']/section/div/a"));
		jobLinks.addAll(jobLinksOther);
		System.out.println(jobLinks.size());
		List<String> url = new ArrayList<String>();
		for (WebElement webElement : jobLinks) {
			url.add(webElement.getAttribute("href"));
		}
		
		for (String string : url) {
			driver.get(string);
			driver.switchTo().frame(0);
			System.out.println(driver.findElement(By.className("app-title")).getText());
			System.out.println(driver.findElement(By.className("location")).getText());
			System.out.println(driver.findElement(By.id("content")).getText());
		}
	}

}
