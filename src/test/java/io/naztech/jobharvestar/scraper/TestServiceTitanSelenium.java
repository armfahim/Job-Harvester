package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Test ServiceTitan jobs site parsing using selenium.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-13
 */
public class TestServiceTitanSelenium extends TestAbstractScrapper{

	private static final String SITE = "https://www.servicetitan.com/job-openings";
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
		List<WebElement> jobLinksE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='hs-content clearfix']/div")));
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(SITE);
		driver.manage().window().maximize();
		List<WebElement> jobLinksE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='hs-content clearfix']/div")));
		
		for (int i = 0;i<jobLinksE.size();i++) {
			List<WebElement> jobs = driver.findElements(By.xpath("//div[@class='span12 widget-span widget-type-widget_container hs-flex']/div[@class='postitions-list']"));
			List<WebElement> anchor = jobs.get(i).findElements(By.tagName("a"));
			System.out.println(anchor.size());
			
			for (WebElement webElement : anchor) {
				System.out.println(webElement.getAttribute("href"));
			}
		}
	}
	
	@Test
	public void testJobDetails() {
		driver.get(SITE);
		driver.manage().window().maximize();
		List<WebElement> jobLinksE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='hs-content clearfix']/div")));
		List<String> url = new ArrayList<String>();
		for (int i = 0;i<jobLinksE.size();i++) {
			List<WebElement> jobs = driver.findElements(By.xpath("//div[@class='span12 widget-span widget-type-widget_container hs-flex']/div[@class='postitions-list']"));
			List<WebElement> anchor = jobs.get(i).findElements(By.tagName("a"));
			for (WebElement webElement : anchor) {
				url.add(webElement.getAttribute("href"));
			}
		}
	}

}
	
