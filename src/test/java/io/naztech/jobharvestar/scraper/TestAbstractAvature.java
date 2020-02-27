package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestAbstractAvature {
	private static WebClient client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(30 * 1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30 * 1000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}
	
	@Test
	public void testPageRoation() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page=client.getPage("https://attglobal.avature.net/careers/SearchJobs");
		List<HtmlElement> el=page.getByXPath("//span[@class='jobPaginationLegend']");
		int totalPage=Integer.parseInt((el.get(0).asText().split("of")[1]).split("results")[0])/20;
		System.out.println(totalPage);
	}
	
	@Test
	public void testSummaryPagesJobs() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page=client.getPage("https://attglobal.avature.net/careers/SearchJobs");
		List<HtmlElement> jobList=page.getByXPath("//main[@id='mainContent']/ul/li");
		for(HtmlElement li: jobList) {
			System.out.println(li.asText());
		}
		
		
	}
}
