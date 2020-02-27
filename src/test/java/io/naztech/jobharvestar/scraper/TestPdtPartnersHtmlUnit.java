package io.naztech.jobharvestar.scraper;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Test Pdt Partners jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-07
 */
public class TestPdtPartnersHtmlUnit extends TestAbstractScrapper {
	private static final String SITE = "https://jobs.pdtpartners.com/";
	private static WebClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(10000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='opening']/a");
		List<String> links = new ArrayList<String>();
		for(HtmlElement el:jobLinksE) {
			links.add(el.getAttribute("href"));
			System.out.println(el.getAttribute("href"));
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(10000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='opening']/a");
		List<String> links = new ArrayList<String>();
		for(HtmlElement el:jobLinksE) {
			links.add(el.getAttribute("href"));
		}
		for(String string:links) {
			page = client.getPage(string);
			client.waitForBackgroundJavaScript(50000);
			List<FrameWindow> frames1 = page.getFrames();
			page = (HtmlPage) frames1.get(0).getEnclosedPage();
			HtmlElement title = (HtmlElement) page.getByXPath("//div[@id='header']/h1").get(0);
			System.out.println(title.asText());
			HtmlElement location = (HtmlElement) page.getByXPath("//div[@class='location']").get(0);
			System.out.println(location.asText());
			System.out.println(page.getElementById("content").asText());
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}
	

}
