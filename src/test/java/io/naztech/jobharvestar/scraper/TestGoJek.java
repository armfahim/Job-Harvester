package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class TestGoJek extends TestAbstractScrapper{
	
	private static final String SITE="https://www.go-jek.com/careers/";
	private static WebDriver driver;
	private static WebDriverWait wait;
	
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		driver.get(SITE);
		wait = new WebDriverWait(driver, 60);
		/*Actions actions = new Actions(driver);
		actions.moveToElement(webElement).click().perform();*/
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,2500)");
		List<WebElement> jobUrlList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//td[@class='details']/a")));
		for (WebElement webElement : jobUrlList) {
			System.out.println("URL: "+webElement.getAttribute("href"));
		}
	}

	@Test
	public void testGetJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String link= "https://www.go-jek.com/careers/detail/?job=115688";
		driver.get(link);
		wait = new WebDriverWait(driver, 60);
		WebElement jobHeader= driver.findElement(By.xpath("//div[@class='title']"));
		WebElement prerequisite= driver.findElement(By.xpath("//div[@class='qualifications']"));
		WebElement desc= driver.findElement(By.xpath("//div[@class='desc']"));
		WebElement applyPanel= driver.findElement(By.xpath("//div[@class='apply-panel']/ul"));
		
		System.out.println("CATEGORY: "+jobHeader.findElement(By.cssSelector("#division")).getText().replace("/", "").trim());
		System.out.println("TITLE: "+jobHeader.findElement(By.cssSelector(".job")).getText().trim());
		System.out.println("LOCATION: "+applyPanel.findElement(By.cssSelector("#location")).getText().trim());
		System.out.println("TYPE: "+applyPanel.findElement(By.cssSelector("#tenture")).getText().replace("employment", "").trim());
		System.out.println("DEADLINE: "+applyPanel.findElement(By.cssSelector("#deadline")).getText().replace("Application deadline", "").trim());
		System.out.println("LOCALDATE: "+parseDate(applyPanel.findElement(By.cssSelector("#deadline")).getText().replace("Application deadline", "").trim(), DF));
		System.out.println("APPLY URL: "+applyPanel.findElement(By.tagName("a")).getAttribute("href").trim());
		
		System.out.println("PREREQUISITE: "+prerequisite.getText().trim());
		System.out.println("DESCRIPTION: "+desc.getText().trim());
		
	}
}
