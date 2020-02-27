package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestGoldmanSachs {
	private static final String URL = "https://uscareers-goldmansachs.icims.com/jobs/50886/compliance%2c-gsam-compliance---aims%2c-analyst/job?hub=7&mobile=false&width=760&height=500&bga=true&needsRedirect=false&jan1offset=360&jun1offset=360";
	private static final String BASIC_INFO_EL_PATH = "//div[@role='list']/dl[@role='listitem']/dd";
	private static WebClient webClient;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = new WebClient(BrowserVersion.FIREFOX_52);
		webClient.waitForBackgroundJavaScript(25 * 1000);
		webClient.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
		webClient.getOptions().setTimeout(30 * 000);
		webClient.setJavaScriptTimeout(25 * 1000);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getCookieManager().setCookiesEnabled(true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = webClient.getPage(URL);
		webClient.waitForBackgroundJavaScript(30*1000);
		//HtmlElement el = (HtmlElement) page.getBody().getByXPath(BASIC_INFO_EL_PATH).get(0);
		WebResponse response = page.getWebResponse();
		System.out.println("\nText: "+response.getResponseHeaders()+"\n");
	}

}
