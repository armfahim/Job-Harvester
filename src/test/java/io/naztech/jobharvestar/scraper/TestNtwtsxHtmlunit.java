package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class TestNtwtsxHtmlunit extends TestAbstractScrapper{
	private static final String SITE = "https://n26.com/en/careers/";
	private static WebClient client = null;
	
	private String baseUrl;


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
		
		this.baseUrl = SITE.substring(0, 15);
		
		HtmlPage page = client.getPage(SITE);
		List<HtmlElement> list = page.getByXPath("//ul[@class='au aw ay ba bc bd hc hd he hf hg']/li/a");
		System.out.println("========================================================");
		System.out.println("List.size(): "+list.size());
		System.out.println("========================================================");
		
		for (HtmlElement row : list) {
			System.out.println(baseUrl + row.getAttribute("href"));
			
		}
	
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		
		String detailPageLink = "https://n26.com/en/careers/positions/1305656";
		
		HtmlPage page = client.getPage(detailPageLink);
		HtmlElement title = page.getFirstByXPath("//h1[@class='ay bs gx gy gz ha hb hc hd he hf hg hh hi hj']");
		System.out.println("Title: "+title.asText().trim());

		List<HtmlElement> list = page.getByXPath("//dd[@class='hk hl hm']");
		
		System.out.println("location: "+list.get(1).asText().trim());
		System.out.println("Category: "+list.get(0).asText().trim());

		HtmlElement elSpec = page.getFirstByXPath("//section[@class='hx hy hz']/div[1]");
		System.out.println("Spec: \n"+elSpec.asText().trim());

		
		
	}
}