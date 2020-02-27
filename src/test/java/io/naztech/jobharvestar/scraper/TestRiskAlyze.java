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
/**
 * @name RiskAlyze jobsite
 * @author Muhammad Bin Farook
 * @since 2019-03-27
 */
public class TestRiskAlyze extends TestAbstractScrapper {
	private static final String JOBSITE_URL = "https://www.riskalyze.com/careers";

	private static WebClient client;

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
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		HtmlPage page = client.getPage(JOBSITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);

		List<HtmlElement> linkAnchor = page.getByXPath("//div[@id='whr_embed_hook']//a");

		for (HtmlElement el : linkAnchor) {

			testJobDetails(el.getAttribute("href"));
		}
	}

	private void testJobDetails(String string)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = client.getPage(string);
		client.waitForBackgroundJavaScript(10 * 1000);
		HtmlElement title = page.getFirstByXPath("//section[@class='section section--header']/h1");
		System.out.println("JOB TITLE: " + title.asText());
		HtmlElement location = page.getFirstByXPath("//section[@class='section section--header']/p[@class='meta']");
		System.out.println("JOB LOCATION: " + location.asText());
		List<HtmlElement> dlist = page.getByXPath("//main[@id='main']/section[@class='section section--text']");
		System.out.println("APPLICATION URL: "+page.getAnchorByText("Apply for this job").getAttribute("href"));

		System.out.println("SPEC: \n" + dlist.get(0).asText().trim() + dlist.get(2).asText().trim());
		System.out.println("REQUIREMENT: \n" + dlist.get(1).asText().trim());
		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------");
		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------");

	}
}
