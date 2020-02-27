package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Test Amazon jobs site parsing using selenium web driver.
 *  
 * @author assaduzzaman.sohan
 * @since 2019-03-13
 */
public class TestDiscord extends TestAbstractScrapper {
	private static final String SITE = "https://discordapp.com/jobs";
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private static WebClient CLIENT = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}

	@Test
	public void testDates() {

	}

	@Test
	public void testGetJobList() {

	}

	@Test
	public void testFirstPage() {
		driver.get(SITE);
		List<WebElement> allJobLink = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
		
		for (int i = 0; i < allJobLink.size(); i++) {
			String Link = allJobLink.get(i).getAttribute("href");
			if (Link.contains("/jobs/"))
				System.out.println(Link);
		}
	}

	@Test
	public void testGetNextPage() throws InterruptedException {

	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://discordapp.com/jobs/4190967002";
		driver.get(Link);
		wait = new WebDriverWait(driver, 50);
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("description-1gVGxt")));
		String title = driver.getTitle();
		System.out.println("Title: "+title.replace("Discord Inc. -", "").trim());
		System.out.println(el.getText());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}
	
}