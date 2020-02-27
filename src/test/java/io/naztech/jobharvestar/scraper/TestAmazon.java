package io.naztech.jobharvestar.scraper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestAmazon {

	String Link = "https://www.amazon.jobs/en/search?offset=0&result_limit=10&sort=relevant&distanceType=Mi&radius=24km&latitude=&longitude=&loc_group_id=&loc_query=&base_query=&city=&country=&region=&county=&query_options=&";

	private static ChromeDriver driver;
	private static WebDriverWait wait;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		getDriver();
	}
	
	
	@Test
	public void test() throws IOException {
		driver.get(Link);
		wait = new WebDriverWait(driver, 50);
		List<WebElement> list = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 0)); 
		List<WebElement> date = driver.findElementsByClassName("posting-date");
		List<WebElement> locationAndId = driver.findElements(By.className("location-and-id"));
		
		for(int i=0; i<list.size(); i++) {
			System.out.println(list.get(i).getAttribute("href"));
			System.out.println(date.get(i).getText().replace("Posted ", "").trim());
			System.out.println(locationAndId.get(i).getText());
		}
		
		
//		for(int i=0; i<list.size(); i++) {
//			System.out.println("=============================================================");
//			System.out.println(list.get(i).getAttribute("href"));
//			System.out.println(date.get(i).getText().replace("Posted ", "").trim());
//			
//			HtmlPage page = CLIENT.getPage(list.get(i).getAttribute("href"));
//			CLIENT.waitForBackgroundJavaScript(10*1000);
//			
//			System.out.println(page.getBody().getOneHtmlElementByAttribute("h1", "class", "title").getAttribute("title"));
//			String[] parts = page.getBody().getOneHtmlElementByAttribute("div", "class", "details-line").asText().split("\\|");
//			System.out.println(parts[0].replace("Job ID:", "").trim());
//			System.out.println(parts[1].trim());
//			
//			System.out.println("=============================================================");
//		}
		
		
		
		WebElement el = driver.findElement(By.cssSelector(".btn.circle.right"));
		
		el.click();
		List<WebElement> list2 = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='job-tile-lists col-12']/div/a"), 9)); 
		List<WebElement> date2 = driver.findElementsByClassName("posting-date");
		
		for(int i=0; i<list2.size(); i++) {
			System.out.println(list2.get(i).getAttribute("href"));
			System.out.println(date2.get(i).getText().replace("Posted ", "").trim());
		}
		
		
		
		
		//for(int i=0; i<list.size(); i++) System.out.println(list.get(i).findElement(By.tagName("a").));
	}

	private static void getDriver() {
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File("webdrivers/chromedriver.exe")).usingAnyFreePort().build();
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);

	}
	
	
}



//System.out.println("===================================================================");
//System.out.println("Job Title = "+title);
//System.out.println("Job Id = "+jobId);
//System.out.println("Job cate = "+cate);
//System.out.println("Location = "+location);
//System.out.println("salary = "+Salary);
//System.out.println("Job closingDate = "+closingDate);
//System.out.println("Job applyUrl = "+applyUrl);
//System.out.println("Job prereq = "+prereq);
//System.out.println("Job des = "+des);
//System.out.println("===================================================================");
