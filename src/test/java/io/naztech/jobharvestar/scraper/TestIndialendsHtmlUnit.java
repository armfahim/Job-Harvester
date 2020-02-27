package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestIndialendsHtmlUnit {
	
	private static Logger log = LoggerFactory.getLogger(HtmlUnitTest_BRK.class);
	private static final String SAMPLE_DETAIL_PAGE_URL = "https://newton.newtonsoftware.com/career/JobIntroduction.action?clientId=8aa005063062881c01309410255e6bdb&id=8aa005063189813001318cbf923c682f";
	private static final String SAMPLE_SUMMARY_PAGE_URL = "https://www.aasaanjobs.com/s/indialends-jobs/";
	private static WebClient webClient;
	private static HtmlPage page;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = new WebClient(BrowserVersion.FIREFOX_52);
		webClient.waitForBackgroundJavaScript(20 * 1000);
		webClient.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
		webClient.getOptions().setTimeout(30 * 1000);
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void testJobSummaryPage() {
		try {
			page = webClient.getPage("https://www.aasaanjobs.com/s/indialends-jobs/");
			//webClient.waitForBackgroundJavaScript(20 * 1000);
			
			
			System.out.println(page.asXml());
			

			List<HtmlElement> row = page.getByXPath("div[@class='row p-top-sm card-custom pos-relative search-highlight search-card cursor-pointer track-search-click redirect-search-click']");
			
			for (HtmlElement htmlElement : row) {
				
				System.out.println(htmlElement.asXml());
				//HtmlElement el = htmlElement.getFirstByXPath("a[class=track-clevertap text-semibold text-gray-darker break-words js-no-action track-search-click line-height-20]");
				//System.out.println(el.getTextContent());
				
			}
		}
		catch (FailingHttpStatusCodeException | IOException e) {

			log.error("Error on testing Summary Page", e, e);
		}
	}

	
	public void testJobDetailPage() {
		try {
			page = webClient.getPage(SAMPLE_DETAIL_PAGE_URL);
			System.out.println(page.getTitleText());
			webClient.waitForBackgroundJavaScript(5000);
			HtmlElement desEl = page.getBody().getOneHtmlElementByAttribute("td", "id", "gnewtonJobDescriptionText");
			System.out.println("Description: " + desEl.getTextContent().trim());
		}
		catch (FailingHttpStatusCodeException | IOException e) {

			log.error("Error on testing Detail Page", e, e);
		}
	}

}
