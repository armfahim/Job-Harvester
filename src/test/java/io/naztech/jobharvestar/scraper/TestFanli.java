package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestFanli extends TestAbstractScrapper{
	private static final String SITE = "https://help.fanli.com/a/about/joinus.html";
	private static String baseUrl = "https://help.fanli.com";
	
	private static WebClient CLIENT = null;
	
	private static ChromeDriver driver;
	private static WebDriverWait wait;

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
		HtmlPage page = CLIENT.getPage(SITE); 
		
		List<HtmlElement> list = page.getBody().getElementsByAttribute("ul", "class", "clearfix");

		for(int i=0; i<list.size(); i++) {
			List<HtmlElement> listE = list.get(i).getElementsByTagName("li");
			for(int j=0; j<listE.size(); j++) {
			String link = baseUrl+"/a/about/joinus.html"+listE.get(j).getElementsByTagName("a").get(0).getAttribute("href");
			String title = listE.get(j).getElementsByTagName("a").get(0).asText();
			System.out.println("Link: "+link);
			System.out.println("Title: "+title);
			}
		}
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE); 
		
		List<HtmlElement> list = page.getBody().getElementsByAttribute("ul", "class", "clearfix");

		for(int i=0; i<list.size(); i++) {
			List<HtmlElement> listE = list.get(i).getElementsByTagName("li");
			for(int j=0; j<listE.size(); j++) {
			String link = baseUrl+"/a/about/joinus.html"+listE.get(j).getElementsByTagName("a").get(0).getAttribute("href");
			String title = listE.get(j).getElementsByTagName("a").get(0).asText();
			System.out.println("Link: "+link);
			System.out.println("Title: "+title);
			System.out.println(listE.get(j).getElementsByTagName("a").get(0).getAttribute("href").replace("#", "").trim());
			String className = listE.get(j).getElementsByTagName("a").get(0).getAttribute("href").replace("#", "").trim();
			System.out.println("Des: "+page.getElementById(className).asText());
			}
		}
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		HtmlPage page = CLIENT.getPage(SITE); 
		
		List<HtmlElement> list = page.getBody().getElementsByAttribute("ul", "class", "clearfix");

		for(int i=0; i<list.size(); i++) {
			List<HtmlElement> listE = list.get(i).getElementsByTagName("li");
			for(int j=0; j<listE.size(); j++) {
			System.out.println(listE.get(j).getElementsByTagName("a").get(0).getAttribute("href").replace("#", "").trim());
			String className = listE.get(j).getElementsByTagName("a").get(0).getAttribute("href").replace("#", "").trim();
			System.out.println("Des: "+page.getElementById(className).asText());
			}
		}
	}
}