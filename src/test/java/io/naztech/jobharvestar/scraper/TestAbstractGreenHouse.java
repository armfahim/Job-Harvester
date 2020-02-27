package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestAbstractGreenHouse extends TestAbstractScrapper {

	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private static WebClient client = null;
	
	private static final String baseUrl = "https://boards.greenhouse.io/embed/job_board?for=illumio&b=https%3A%2F%2Fwww.illumio.com%2Fcareer-openings";
										
// 	private static final String SITE = "https://gusto.com/about/careers";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	
	@Test
	public void test() throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		
//		String link = "https://gusto.com/about/careers";
		
		HtmlPage page = client.getPage(baseUrl);
		client.waitForBackgroundJavaScript(TIME_5S*10);
		
		System.out.println(page.asText());
		
		HtmlPage pageI = client.getPage(page.getElementById("grnhse_iframe").getAttribute("src"));
		client.waitForBackgroundJavaScript(TIME_5S);
		System.out.println(pageI.asText());
	}

	@Test
	public void testGetFirstPage() throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(55, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
		
		wait = new WebDriverWait(driver, 70);

		driver.get(baseUrl);
		List<WebElement> list = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));
		System.out.println("list.size(): "+list.size());
		
		List<String> allJobLink = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			String Link = list.get(i).getAttribute("href");
			if (Link == null) continue;
			if (Link.contains("/jobs/")) allJobLink.add(Link);
		}
		
		System.out.println("allJobLink.size(): "+allJobLink.size());
		for (int i = 0; i < allJobLink.size(); i++) {
			System.out.println(allJobLink.get(i));
		}
		driver.quit();
	}

	@Test
	public void testGetJobLinks() throws IOException {
		
		HtmlPage page = client.getPage(baseUrl);
		client.waitForBackgroundJavaScript(TIME_5S);

		List<HtmlElement> list = page.getBody().getElementsByTagName("a");
		List<String> allJobLink = new ArrayList<>();
		System.out.println("list.size(): "+list.size());
		
		for(int i=0; i<list.size(); i++) {
			String Link = list.get(i).getAttribute("href");
			if(Link.contains("gh_jid")) {
				allJobLink.add(Link);
			}
		}
		
		System.out.println("allJobLink.size(): "+allJobLink.size());
		for(int i=0; i<allJobLink.size(); i++) {
			System.out.println(allJobLink.get(i));
		}
	}
	
	@Test
	public void testGetJobDetails() throws IOException {
	//	String link = "http://www-illumio-com.sandbox.hs-sites.com/career-openings?gh_jid=4253928002";
//		String link = "https://boards.greenhouse.io/gusto/jobs/590568";
		Document doc= Jsoup.connect(baseUrl).get();
		System.out.println(doc.select("section.level-0 > h3").text().trim());
		System.out.println(doc.select("div > h1.app-title").get(0).text().trim());
		System.out.println(doc.select("div.location").get(0).text().trim());
		System.out.println(doc.select("div#content").get(0).text().trim());

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

}