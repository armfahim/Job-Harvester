package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestInstacart extends TestAbstractScrapper{
	private static final String SITE = "https://careers.instacart.com/openings/";
	
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
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(TIME_10S);
		
		List<String> allJobLink = new ArrayList<>();
		List<HtmlElement> list = page.getBody().getElementsByAttribute("tr", "class", "odd");
		for(int i=0; i<list.size(); i++) allJobLink.add(list.get(i).getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
		list = page.getBody().getElementsByAttribute("tr", "class", "even");
		for(int i=0; i<list.size(); i++) allJobLink.add(list.get(i).getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
		
		System.out.println(allJobLink.size());
		for(int i=0; i<allJobLink.size(); i++)System.out.println(allJobLink.get(i)); 
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://boards.greenhouse.io/instacart/jobs/1542944";
		HtmlPage page = CLIENT.getPage(Link); 
		CLIENT.waitForBackgroundJavaScript(TIME_10S);

		System.out.println("Title = "+page.getBody().getOneHtmlElementByAttribute("h1", "class", "app-title").asText());
		System.out.println("Location = "+page.getBody().getOneHtmlElementByAttribute("div", "class", "location").asText());
		
		System.out.println("Des: "+page.getBody().getOneHtmlElementByAttribute("div", "id", "content").asText());
		
		HtmlElement appylyBtn = page.getBody().getOneHtmlElementByAttribute("a", "id", "apply_button");
		appylyBtn.click();
		System.out.println("Apply Url = "+page.getBaseURI());
		
		
		
	}
}