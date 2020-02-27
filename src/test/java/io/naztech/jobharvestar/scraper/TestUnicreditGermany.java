package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class TestUnicreditGermany {
	private static final String SITE_URL = "https://recruiting.unicreditgroup.eu/ucihr/bc/webdynpro/job_search?sap-language=DE&sap-wd-configId=/UCIHR/HRRCF_A_UNREG_JOB_SEARCH%23#";
	private static final String EL_SEL = "div[draggable='false'][id='WD35']";
	private static final String ROW_EL_PATH = "//tr[@class='lsCondensed urST5SelColUiGeneric']/td[2]/a";
	private static WebDriver driver;
	private static WebDriverWait wait;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initDriver();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.quit();
	}

	@Test
	public void testPageLoaded() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(SITE_URL);
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(EL_SEL)));
		//assertEquals("Start", el.getText());
		el.click();
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_EL_PATH)));
		assertEquals(15, rowList.size());
	}
	
	private static void initDriver() {
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File("webdrivers/chromedriver.exe")).usingAnyFreePort().build();
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);

	}

}
