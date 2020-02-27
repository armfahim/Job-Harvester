package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @name Singapore Exchange Job site parser Test
 * @author Mahmud Rana
 * @since 2019-02-11
 * @url https://career.sgx.com/viewjobs.php
 */

public class TestSingaporeExchange {
	private static final String DETAIL_PAGE_URL = "https://career.sgx.com/viewjobs.php?jid=1674";
	private static final String COLLAPSE_EL_PATH = "//div[@class='CollapsiblePanelContent']";
	private static final String APPLY_EL_PATH = "//div[@id='buttonApplyJobs']/div/a";
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
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(DETAIL_PAGE_URL);
		HtmlElement el = (HtmlElement) page.getBody().getByXPath(COLLAPSE_EL_PATH).get(1);
		System.out.println("Total String: " + el.getTextContent());
		// assertEquals("Assistant Vice President, Business Continuity Management",
		// el.getTextContent().split(":")[1].trim());
	}

	@Test
	public void testApplyLink() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(DETAIL_PAGE_URL);
		HtmlElement el = (HtmlElement) page.getBody().getByXPath(APPLY_EL_PATH).get(1);
		assertEquals("/jobseeker/applyjob.php?jid=1674", el.getAttribute("href"));
	}

}
