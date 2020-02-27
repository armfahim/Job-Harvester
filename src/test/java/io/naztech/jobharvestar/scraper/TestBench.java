package io.naztech.jobharvestar.scraper;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.lang.IllegalMonitorStateException;
import org.junit.BeforeClass;
import java.lang.ClassCastException;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;




/**
 * Bench job site scraper. <br>
 * URL:https://bench.co/careers/#current_openings
 * 
 * @author Asadullah Galib
 * @since 2019-03-27
 */
 
public class TestBench extends TestAbstractScrapper{

	private static final String SITE = "https://bench.co/careers/#current_openings";
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
	}
	
	@Test
	public void testGetJobList() throws InterruptedException, IllegalMonitorStateException, TimeoutException {
		driver.get(SITE);
		Thread.sleep(TIME_4S);
		List<WebElement> jobLinks = driver.findElements(By.xpath("//tbody/tr/td/a"));
		for (WebElement webElement : jobLinks) {
			System.out.println(webElement.getAttribute("href"));
		}
		

	}
	
	@Test
	public void testGetJobDetail() throws InterruptedException, IllegalMonitorStateException, TimeoutException,ClassCastException {
			driver.get(SITE);
	Thread.sleep(TIME_4S);
	List<WebElement> jobLinks = driver.findElements(By.xpath("//tbody/tr/td/a"));
	for (WebElement webElement : jobLinks) {
		System.out.println(webElement.getAttribute("href"));
	}
		System.out.println(jobLinks.size());
		List<String> url = new ArrayList<String>();
		for (WebElement webElement : jobLinks) {
			url.add(webElement.getAttribute("href"));
		}
		int i=1;
		for (String string : url) {
			
			driver.get(string);
			System.out.println(i);
			List<WebElement> title =wait.until(ExpectedConditions
					.numberOfElementsToBeMoreThan(By.xpath("//header[@class='WidgetValueProp__ValuePropHeader-sc-902n5r-0 eBYsrZ']"), 0));
			System.out.println(title.get(0).getText());
			WebElement spec=driver.findElementById("article-body");
			System.out.println(spec.getText());
			i++;
			
		}
	}

}

