package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestMeniga {
	private static final String site_url="https://jobs.50skills.com/meniga/";
	private static ChromeDriverService service;
	private ChromeDriver driver;
	private WebDriverWait wait;
	
	@BeforeClass
	public static void beforeClass() {
		service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
				.usingAnyFreePort().build();
	}
	
	
	@Before
	public void setUp() throws Exception {
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(false));
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	public void test() throws  MalformedURLException, IOException, InterruptedException {
		driver.get(site_url);
		List<WebElement>jobList=wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='styles__JobEntry-sc-1or87b6-0 elwKCY']")));
		for(int i=0;i<jobList.size();i++)
		{
			jobList.get(i).click();
			Thread.sleep(4000);
			String title = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@class='JobListingTitle-sc-1bz7m4e-0 jydTtv']"))).getText();
			String spec = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='JobListingDetail__Html-sc-1aduvcq-1 Idwq']"))).getText();
			System.out.println(title);
			System.out.println(spec);
			driver.navigate().back();
			jobList=wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='styles__JobEntry-sc-1or87b6-0 elwKCY']")));
			
		}
		System.out.println(jobList.size());
		
		
		
	}

}
