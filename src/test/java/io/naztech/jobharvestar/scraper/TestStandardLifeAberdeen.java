package io.naztech.jobharvestar.scraper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Standard Life Aberdeen job site parser test.
 * https://standardlife.taleo.net/careersection/global+sl+external+career+site+eng/jobsearch.ftl
 * 
 * @author Nuzhat Tabassum
 * @since 2019-02-14
 */
public class TestStandardLifeAberdeen {
	private static WebClient client;
	private static final String SITE_URL = "https://standardlife.taleo.net/careersection/global+sl+external+career+site+eng/jobsearch.ftl";
	private static final String APP_LINK_EL_PATH = "//a[@class='result-list-button']";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.CHROME);
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
	public void testUrl() {
		System.out.println(SITE_URL.substring(0, 30));
	}

	@Test
	public void testTotalPage() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			HtmlElement el = page.getBody().getOneHtmlElementByAttribute("span", "id", "currentPageInfo");
			System.out.println(el);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testData() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			HtmlElement el = page.getBody().getFirstByXPath(APP_LINK_EL_PATH);
			System.out.println(el);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
