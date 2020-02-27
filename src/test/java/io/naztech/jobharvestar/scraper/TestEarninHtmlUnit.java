package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Earnin Job site parse test in HtmlUnit <br>
 * https://www.earnin.com/careers
 * 
 * @author jannatul.maowa
 * @since 2019-04-01
 */
public class TestEarninHtmlUnit extends TestAbstractScrapper {
	private static WebClient client;
	private static String url = "https://www.earnin.com/jobs?gh_jid=1152779";

	@Before
	public void setUp() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void testDetailPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(url);
		HtmlElement body = page.getBody();
		HtmlElement iframeLink = body.getFirstByXPath("//iframe[@id='grnhse_iframe']");
		page = client.getPage(iframeLink.getAttribute("src"));
		body = page.getBody();
		System.out.println(body.getOneHtmlElementByAttribute("h1", "class", "app-title").getTextContent().trim());
		System.out.println(body.getOneHtmlElementByAttribute("div", "class", "location").getTextContent().trim());
		System.out.println(
				"Specification:" + body.getOneHtmlElementByAttribute("div", "id", "content").getTextContent().trim());
	}
}
