package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestOcbc {
	private static String SITE_URL = "https://ocbc.taleo.net/careersection/ocbc_my_external+1st+subm/jobsearch.ftl?lang=en&#";
	private static final String DETAIL_PAGE_URL = "https://ocbc.taleo.net/careersection/ocbc_chn_external+1st+submmission/jobdetail.ftl?job=1800013C&tz=GMT%2B06%3A00";
	private static final String TITLE_EL_ID = "requisitionDescriptionInterface.reqTitleLinkAction.row1";
	private static final String ROW_EL_PATH = "//table[@id='jobs']/tbody/tr/th/div/div/span/a";
	private static final String NODE_PATH = "//table[@id='jobs']/tbody";
	private static final String PAGING_EL_PATH = "//*[@id=\"jobPager\"]/span[2]/ul/li/a";
	private static WebClient client;
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private int waitCounter = 0;

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
		if (client != null)
			client.close();
		if (driver != null)
			driver.quit();
	}

	// Does not work
	@Test
	public void testRowLoadedByClient()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		client.waitForBackgroundJavaScriptStartingBefore(10000);
		HtmlPage page = client.getPage(SITE_URL);
		client.loadDownloadedResponses();
		client.waitForBackgroundJavaScript(40 * 1000);
		// HtmlElement script = page.getBody().getOneHtmlElementByAttribute("script",
		// "src", "/careersection/2017PRD.4.0.57.3.0/js/facetedsearch/URLBuilder.js");
		// System.out.println("Script: "+script.asText().toString());
		DomNode node = page.getBody().getFirstByXPath(NODE_PATH);
		System.out.println("Node Size: " + node.getChildNodes().size());
		while (node.getChildNodes().size() <= 1) {
			if (waitCounter >= 10)
				break;
			System.out.println(waitCounter + " Wait for row loaded...");
			Thread.sleep(2000);
			client.waitForBackgroundJavaScript(40000);
			node = page.getBody().getFirstByXPath(NODE_PATH);
			waitCounter++;
		}
		List<HtmlElement> rowList = page.getBody().getByXPath(ROW_EL_PATH);
		assertEquals(11, rowList.size());
	}

	@Test
	public void testRowLoadedByDriver() {
		try {
			driver.get(SITE_URL);
			List<WebElement> rowList = wait
					.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROW_EL_PATH), 0));
			System.out.println("Total Row: " + rowList.size());
			assertEquals(11, rowList.size());
		} catch (TimeoutException e) {
			System.out.println("Page Load Time Out: " + e.getMessage());
		}
	}

	@Test
	public void testDetailPageLoaded() {
		try {
			HtmlPage page = client.getPage(DETAIL_PAGE_URL);
			client.waitForBackgroundJavaScript(8 * 1000);
			HtmlElement el = (HtmlElement) page.getElementById(TITLE_EL_ID);
			String s = el.getTextContent().trim();
			assertEquals("Relationship Manager, Business Banking", s);
		} catch (FailingHttpStatusCodeException | IOException e) {

		}
	}

	@Test
	public void testPagination() throws InterruptedException {
		try {
			driver.get(SITE_URL);
			List<WebElement> rowList = wait
					.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROW_EL_PATH), 0));
			
			int page = 1;
			do {
				List<WebElement> pagination = driver.findElements(By.xpath(PAGING_EL_PATH));
				System.out.println("\n-------Page"+page+"---------");
				for (WebElement row : rowList) {
					System.out.println("Title: "+row.getText());
				}
				Thread.sleep(RandomUtils.nextInt(3000,5000));
				pagination.get(++page).click();
				Thread.sleep(5000);
				rowList.clear();
				rowList = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(ROW_EL_PATH), 0));
				//page++;
			} while (page < 5);
		} catch (TimeoutException e) {
			System.out.println("Something Wrong...");
		}
	}
}
