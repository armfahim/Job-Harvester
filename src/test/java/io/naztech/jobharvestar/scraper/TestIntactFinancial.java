package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class TestIntactFinancial{
	private static final String PAGE_URL = "https://careers.intact.ca/ca/en/search-results";
	private static final String ROW_EL_PATH = "/html/body/div[2]/div/div[2]/div/div[2]/section/div/div/div/div[2]/div[2]/ul/li/div[1]/a";
	private static final String PAGING_EL_PATH = "/html/body/div[2]/div/div[2]/div/div[2]/section/div/div/div/div[3]/ul/li[6]/a";
	private static final String JOB_COUNT_EL_PATH = "/html/body/div[2]/div/div[2]/div/div[2]/section/div/div/div/div[2]/div[1]/div[2]/div[2]/div/div[1]/span[1]";
	private static ChromeDriver driver;
	private static WebDriverWait wait;


	@BeforeClass
	public static void beforeClass() {
		ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
				.usingAnyFreePort().build();
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(70, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
	}


	@AfterClass
	public static void afterClass() {
		driver.quit();
	}

	@Test
	public void testRowCount() {
		driver.get(PAGE_URL);
		List<WebElement> row = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_EL_PATH)));
		assertEquals(50, row.size());
	}
	
	@Test
	public void testPagination() throws InterruptedException {
		driver.get(PAGE_URL);
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PAGING_EL_PATH)));
		Thread.sleep(2000);
		el.click();
		Thread.sleep(2000);
		List<WebElement> row = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_EL_PATH+"/h4")));
		assertEquals("Directeur, Communications internes", row.get(0).getText());
	}
	
	@Test
	public void testTotalJobCount() {
		driver.get(PAGE_URL);
		WebElement el = driver.findElement(By.xpath(JOB_COUNT_EL_PATH));
		assertEquals("169", el.getText());
	}
	
}
