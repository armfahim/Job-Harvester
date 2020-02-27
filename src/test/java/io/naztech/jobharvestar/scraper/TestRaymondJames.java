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

public class TestRaymondJames extends TestAbstractScrapper {
	private static final String SITE = "https://jobs.raymondjames.com/search-jobs";
	private static WebClient client = null;
	private static final String ROW_ANCHOR_PATH = "//section[@id='search-results-list']/ul/li/a";
	private static final String NEXT_EL_PATH = "//a[@class='next']";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(30 * 1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.setJavaScriptTimeout(30 * 1000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		int i = 0;
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		
		do {
			List<HtmlElement> rowList = page.getBody().getByXPath(ROW_ANCHOR_PATH);
			for (HtmlElement row : rowList) {
				System.out.println(row.getAttribute("href"));
			}
			HtmlElement el = page.getBody().getFirstByXPath(NEXT_EL_PATH);
			page = el.click();
			client.waitForBackgroundJavaScriptStartingBefore(15 * 1000);
			client.waitForBackgroundJavaScript(20 * 1000);
			rowList = page.getBody().getByXPath(ROW_ANCHOR_PATH);
			i++;
		} while(i<24);
	}
}