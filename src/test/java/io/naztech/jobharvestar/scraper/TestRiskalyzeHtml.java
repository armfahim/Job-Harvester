package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * riskalyze job site scraper. <br>
 * URL: https://www.riskalyze.com/careers
 * 
 * @author Md. Sanowar Ali
 * @since 2019-05-12
 */
public class TestRiskalyzeHtml extends TestAbstractScrapper {
	private static WebClient client;
	private static final String SITE_URL = "https://www.riskalyze.com/careers";
	int pageCount;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}
	
	//Done
	@Test
	public void testgetSummaryPages() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(5 * 1000);
		List<HtmlElement> aList = page.getByXPath("//h3[@class='whr-title']/a");
		for(int i=0;i<aList.size();i++) {
		System.out.println("Job URL: " + aList.get(i).getAttribute("href"));
		}
	}

	@Test
	public void testgetJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {	
	HtmlPage page = client.getPage(SITE_URL);
	client.waitForBackgroundJavaScript(5 * 1000);
	List<HtmlElement> aList1 = page.getByXPath("//h3[@class='whr-title']/a");
	for (int i = 0; i < aList1.size(); i++) {
		String jobURL = aList1.get(i).getAttribute("href");
		HtmlPage page2 = client.getPage(jobURL);
		client.waitForBackgroundJavaScript(15 * 1000);
		HtmlElement el = page2.getBody().getFirstByXPath("//section[@class='section section--header']/h1");
		HtmlElement elLocation = page2.getBody().getFirstByXPath("//section[@class='section section--header']/p[2]");
		HtmlElement elSpec = page2.getFirstByXPath("//section[@class='section section--text']");
		
		System.out.println(el.getTextContent().trim());
		System.out.println(elLocation.getTextContent().trim());
		System.out.println(elSpec.asText().trim());
		System.out.println("......................................................");
		}
	}
	
	@Test
	public void testTotalJob() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(5 * 1000);
		List<HtmlElement> aList = page.getByXPath("//h3[@class='whr-title']/a");
		System.out.println("Job URL: " + aList.size());
	}
}
