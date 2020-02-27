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

public class TestOttobockHtmlunit extends TestAbstractScrapper {
	private static WebClient client;
	private static final String SITE_URL = "https://shop.ottobock.us/Careers";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}
	
	@Test
	public void testgetSummaryPages() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		    HtmlPage page = client.getPage(SITE_URL);
		    client.waitForBackgroundJavaScript(5 * 1000);
			//List<HtmlElement> url = new ArrayList<String>();
			List<HtmlElement> jobUrl = page.getByXPath("//table[@class='table table-striped']/tbody/tr/td/a");
			for (int i = 0; i < jobUrl.size(); i++) 
			{
				String jobURL = jobUrl.get(i).getAttribute("href");
				HtmlPage page2 = client.getPage(jobURL);
				client.waitForBackgroundJavaScript(10 * 1000);
				HtmlElement elTitle = page2.getBody().getFirstByXPath("//div[@class='job-description-details-sub-container']");
				System.out.println(elTitle.getTextContent().trim());
			} 
		}
	
}