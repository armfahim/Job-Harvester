package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestInsuranceAustraliaHtmlunit extends TestAbstractScrapper{
	private static final String SITE = "https://careers.iag.com.au/viewalljobs/";
	private static WebClient client = null;
	
	String baseUrl = "https://careers.iag.com.au";
	String tailUrl = "/?q=&sortColumn=referencedate&sortDirection=desc";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
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

		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(TIME_10S);
		
		List<HtmlElement> list = page.getBody().getElementsByAttribute("li", "class", "col-sm-6 col-md-6 col-xs-6");

		for (HtmlElement it : list) {
			String Link = baseUrl + it.getElementsByTagName("a").get(0).getAttribute("href"); // Search by Category
			
			HtmlPage page2 = client.getPage(Link);
			client.waitForBackgroundJavaScript(TIME_10S); // job link page
			
			String parts[] = page2.getBody().getOneHtmlElementByAttribute("span", "class", "paginationLabel").asText().split("of");
			int totalPage = getPageCount(parts[1].trim(), 25);

			getSummaryPage(page2);
			
			
			for(int i=1; i<totalPage; i++) {
				String nextPage = Link + i*25 + tailUrl;
				page2 = client.getPage(nextPage);
				client.waitForBackgroundJavaScript(TIME_10S);
				
				getSummaryPage(page2);
			}
			
		}

	}
	
	
	
	private void getSummaryPage(HtmlPage page) {
		
		List<HtmlElement> list2 = page.getBody().getElementsByAttribute("tr", "class", "data-row clickable");
		
		for (HtmlElement it2 : list2) {

			HtmlElement el = it2.getElementsByTagName("td").get(0).getElementsByTagName("span").get(0).getElementsByTagName("a").get(0);
			System.out.println(baseUrl + el.getAttribute("href"));
			System.out.println("Title: "+el.asText());
			
			System.out.println("Location: "+it2.getElementsByTagName("td").get(1).asText());
			System.out.println("Type: "+it2.getElementsByTagName("td").get(2).asText());
			
		}
	}
	
	
	

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://careers.iag.com.au/job/Sydney-CBD-Advisor%2C-Injury-Management-%28CTP%29-New-2000/539211901/";

		HtmlPage page = client.getPage(Link);
		client.waitForBackgroundJavaScript(TIME_10S);

		System.out.println("Job Spec: "+ page.getBody().getElementsByAttribute("div", "class", "col-xs-12 fontalign-left").get(1).asText().trim());
	}
}