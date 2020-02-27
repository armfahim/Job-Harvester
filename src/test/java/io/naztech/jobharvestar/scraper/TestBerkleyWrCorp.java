package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author tohedul.islum
 *
 */
public class TestBerkleyWrCorp {
	private static WebClient webClient;
	private static final String SITE_URL = "https://gwlcareers-greatwestlife.icims.com/jobs/search";
	private static final String CONTAINER_FRAME = "icims_content_iframe";
	
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
	public void TestJobPerPage() {
		try {
			HtmlPage pageM =webClient.getPage(SITE_URL);
			HtmlPage pageI = (HtmlPage) pageM.getFrameByName(CONTAINER_FRAME).getEnclosedPage();
			webClient.waitForBackgroundJavaScript(30 * 1000);
			HtmlElement el = pageI.getBody().getElementsByAttribute("a", "target", "_self").get(3);
			String s = el.getAttribute("href");
			int pageNo=Integer.parseInt(s.substring(s.indexOf('=') + 1, s.indexOf('&')));
			System.out.println(pageNo);
		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println("Connection Failed");
		}
	}
	
}
