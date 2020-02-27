package io.naztech.jobharvestar.scraper;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Xapo jobs site parse <br>
 * url https://xapo.com/careers/#opportunities
 * 
 * @author sohid.ullah
 * @since 2019-03-31
 */
public class TestXapoHTMLUnit extends TestAbstractScrapper {
	private static final String JOBSITE_URL = "https://xapo.com/careers/#opportunities";
	private static WebClient webClient = null;

	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testSummaryPage() {
		driver.get("https://xapo.com/careers/#opportunities");
		List<WebElement> rowList = wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//li[@class='BambooHR-ATS-Jobs-Item']/a")));

		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		int totalJob = rowList.size();

		for (int i = 0; i < totalJob; i++) {
			System.out.println(rowList.get(i).getAttribute("href"));
		}

	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		try {
			HtmlPage jobDetailsPage = webClient.getPage("https://xapo.bamboohr.com/jobs/view.php?id=28");
			try {
				HtmlElement jobDetailsElement = jobDetailsPage.getBody();

				HtmlElement headElement = jobDetailsElement.getOneHtmlElementByAttribute("div", "class",
						"col-xs-12 col-sm-8 col-md-12");
				String jobTitle = headElement.getFirstElementChild().asText();
				String jobDesc = jobDetailsElement.getOneHtmlElementByAttribute("div", "class", "col-xs-12").asText();
				String[] categoryAndLocation = headElement.getElementsByTagName("span").get(0).asText().split("–");

				String category = categoryAndLocation[0];
				String location = categoryAndLocation[1];
				System.out.println(location);

			} catch (ElementNotFoundException e) {
				System.out.println("Element not found " + e);
			}
		} catch (FailingHttpStatusCodeException | IOException e) {

		}

	}

}
