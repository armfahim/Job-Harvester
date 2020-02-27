package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 * Test Ellington Group jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-12
 */
public class TestServiceTitanHtmlUnit extends TestAbstractScrapper{

	private static final String SITE = "https://www.servicetitan.com/job-openings";
	private static WebClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='hs-content clearfix']/div");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<HtmlElement> jobs = page.getByXPath("//div[@class='span12 widget-span widget-type-widget_container hs-flex']/div[@class='postitions-list']");
		for (int i = 0;i<jobs.size();i++) {
			List<HtmlElement> anchor = jobs.get(i).getElementsByTagName("a");
			System.out.println(anchor.size());
			for (HtmlElement htmlElement1 : anchor) {
				System.out.println(htmlElement1.getAttribute("href"));
			}
		}
	}
	
	@Test
	public void testJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<HtmlElement> jobs = page.getByXPath("//div[@class='span12 widget-span widget-type-widget_container hs-flex']/div[@class='postitions-list']");
		List<String> url = new ArrayList<String>();
		for (int i = 0;i<jobs.size();i++) {
			List<HtmlElement> anchor = jobs.get(i).getElementsByTagName("a");
			for (HtmlElement htmlElement1 : anchor) {
				url.add(htmlElement1.getAttribute("href"));
			}
		}
		
		for (String string : url) {
			page = client.getPage(string);
			HtmlElement title = page.getFirstByXPath("//h1[@class = 'app-title']");
			System.out.println(title.asText());
			HtmlElement location = page.getFirstByXPath("//div[@class = 'location']");
			System.out.println(location.asText());
			System.out.println(page.getElementById("content").asText());//spec
		}
	}

}
