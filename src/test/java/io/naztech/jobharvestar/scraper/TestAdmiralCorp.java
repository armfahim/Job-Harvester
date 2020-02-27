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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class TestAdmiralCorp {
	private static String SITE_URL = "https://admiraljobs.co.uk/vacancies/";
	private static final String DET_URL = "https://admiraljobs.co.uk/vacancies/3227/retentions_consultant/";
	private static final String SRCH_EL_ID = "search_submit";
	private static final String ROW_EL_PATH = "/html/body/div[2]/div[1]/div[3]/div[2]/div/div/div[1]/div/div/h3/a";
	private static final String TITLE_EL_PATH = "/html/body/div[2]/div[1]/div[3]/div/div/div/div[1]/div/h2";
	private static WebClient client;
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initClient();
		initDriver();
	}

	private static void initDriver() {
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File("webdrivers/chromedriver.exe")).usingAnyFreePort().build();
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);

	}

	private static void initClient() {
		client = new WebClient(BrowserVersion.CHROME);
		client.setJavaScriptTimeout(10 * 1000);
		client.getCookieManager().setCookiesEnabled(true);
		client.getOptions().setUseInsecureSSL(true);
		client.getOptions().setTimeout(30 * 1000);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setGeolocationEnabled(true);
		client.setAjaxController(new NicelyResynchronizingAjaxController());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.quit();
	}

	@Test
	public void testRowLoaded() throws InterruptedException {
		driver.get(SITE_URL);
		WebElement srchBtn = driver.findElementById(SRCH_EL_ID);
		Thread.sleep(2000);
		srchBtn.click();
		Thread.sleep(5000);
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_EL_PATH)));
		System.out.println("Total Row: "+rowList.size());
	}
	
	@Test
	public void testDetailPageLoaded() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(DET_URL);
		client.waitForBackgroundJavaScript(10*1000);
		HtmlElement el = page.getBody().getFirstByXPath(TITLE_EL_PATH);
		System.out.println("Title: "+el.getTextContent());
		assertEquals("Retentions Consultant", el.getTextContent());
	}

	
}
