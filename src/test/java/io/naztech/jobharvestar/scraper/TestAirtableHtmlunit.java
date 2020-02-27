package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestAirtableHtmlunit extends TestAbstractScrapper{
	private static final String SITE = "https://airtable.com/jobs";
	private static WebClient CLIENT = null;

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
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		//System.out.println(page.asText()); 
		
		List<HtmlElement> list = page.getBody().getByXPath("//div[@id='jobs-root']/div/div");
		List<HtmlElement> readMore = page.getBody().getElementsByAttribute("a", "class", "understroke thick pointer link-quiet flex items-center");
		System.out.println("list Size = "+list.size());
		
		for(int i=0; i<list.size(); i++) {
			System.out.println("Title = "+list.get(i).getElementsByTagName("div").get(0).getElementsByTagName("div").get(0).getElementsByTagName("h2").get(0).asText());
			readMore.get(i).click();
			System.out.println("========================================================");
			//System.out.println(page.getBody().getOneHtmlElementByAttribute("div", "class", "lg-col-6 col-12 pt4 lg-pl4 lg-pt0").asText());
			System.out.println(page.getBaseURI());
			System.out.println("========================================================");
		}
		
		
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {

	}
}