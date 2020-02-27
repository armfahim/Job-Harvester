package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Testklarna extends TestAbstractScrapper{
	private static final String SITE = "https://www.klarna.com/careers/openings/";
	
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
		CLIENT.waitForBackgroundJavaScript(TIME_5S);

		List<HtmlElement> list = page.getBody().getElementsByAttribute("a", "class", "kl-card kl-card--job");
		System.out.println("List: "+list.size());
		
		for(int i=0; i<list.size(); i++) {
			System.out.println(list.get(i).getAttribute("href"));
		}
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://jobs.lever.co/klarna/8d68bf51-36c2-4b76-9f74-514ff9a13023";
		HtmlPage page = CLIENT.getPage(Link); 
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		
		HtmlElement el =page.getBody().getOneHtmlElementByAttribute("div", "class", "posting-headline");
		HtmlElement el2 =page.getBody().getOneHtmlElementByAttribute("div", "class", "posting-categories");
		System.out.println("Title = "+el.getElementsByTagName("h2").get(0).asText());
		System.out.println("Location = "+el2.getElementsByTagName("div").get(0).asText());
		System.out.println("Cate = "+el2.getElementsByTagName("div").get(1).asText());
		System.out.println("Apply Url = "+page.getBody().getOneHtmlElementByAttribute("a", "class", "postings-btn template-btn-submit hex-color").getAttribute("href"));
		
		System.out.println("Des: "+page.getBody().getOneHtmlElementByAttribute("div", "class", "section page-centered").asText());
		
		
	}
}