package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestAmazonHtmlunit extends TestAbstractScrapper{
	private static final String SITE = "https://www.amazon.jobs/en/search?base_query=&loc_query=";
	private static WebClient CLIENT = null;

//	private static ChromeDriver driver;
//	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void test() throws IOException {

	}


	@Test
	public void testDates() {
		
	}

	@Test
	public void testGetJobList() {
		
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText()); // Necessary Data Not Found 
		System.out.println(page.asXml());  // Necessary Data Not Found
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://www.amazon.jobs/en/jobs/SF190021770/hvh-launch-team-member-for-brs1";

		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(10 * 1000);

		//Necessary DataFound 
		System.out.println("Job Title: "+ page.getBody().getOneHtmlElementByAttribute("h1", "class", "title").getAttribute("title"));
		System.out.println("Job ApplicationUrl: "+ page.getBody().getOneHtmlElementByAttribute("a", "id", "apply-button").getAttribute("href"));
		System.out.println("Job Description: "+ page.getBody().getOneHtmlElementByAttribute("div", "class", "section description").asText());
		System.out.println("Job setPrerequisite: "+ page.getBody().getOneHtmlElementByAttribute("div", "class", "section").asText());
	}
}