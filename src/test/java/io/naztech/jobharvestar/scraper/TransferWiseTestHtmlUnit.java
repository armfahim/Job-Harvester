package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

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

/**
 * @author Fahim Reza
 *
 */

public class TransferWiseTestHtmlUnit {
	private static WebClient client;
	private static final String SITE_URL = "https://www.traveloka.com/en/careers";

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
		
	}

	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);
		Thread.sleep(5*1000);
		List<HtmlElement> jobList = page.getBody().getByXPath("//div[@class='tv-wrapper tv-job-posting']//div/div/a");

	}
	
	

}
