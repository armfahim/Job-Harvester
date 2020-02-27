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
 * Test HeartFlow jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-12
 */
public class TestHeartFlowHtmlUnit extends TestAbstractScrapper{

	private static final String SITE = "https://www.heartflow.com/careers";
	private static WebClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
		client.waitForBackgroundJavaScript(20 * 1000);
		client.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
		client.getOptions().setTimeout(30 * 1000);
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(TIME_5S);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='cell-span-12']/div/div");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testGetFristPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(TIME_5S);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='cell-span-12']/div/div");
		for (HtmlElement htmlElement : jobLinksE) {
			System.out.println(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println(htmlElement.getElementsByTagName("div").get(1).getTextContent());
		}
	}
	
	@Test
	public void testGetJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(TIME_5S);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='cell-span-12']/div/div");
		List<String> jobLink = new ArrayList<String>();
		for (HtmlElement htmlElement : jobLinksE) {
			jobLink.add(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println(htmlElement.getElementsByTagName("div").get(1).getTextContent());
		}
		
		for (String string : jobLink) {
			page = client.getPage(string);
			client.waitForBackgroundJavaScript(TIME_5S);
			HtmlElement category = page.getFirstByXPath("//p[@class='jv-job-detail-meta']");
			String[] cat = category.getTextContent().trim().split(".");
			System.out.println(cat[0]);
			HtmlElement spec = page.getFirstByXPath("//div[@class='jv-job-detail-description']");
			System.out.println(spec.getTextContent().trim());
		}
	}

}
