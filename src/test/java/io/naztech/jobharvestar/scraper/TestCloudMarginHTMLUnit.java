package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * URL: https://cloudmargin.com/careers/
 * 
 * @author sohid.ullah
 * @since 2019-03-27
 * 
 **/
public class TestCloudMarginHTMLUnit {
	private static final String JOBSITE_URL = "https://cloudmargin.bamboohr.com/jobs/";
	private static WebClient webClient = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = new WebClient(BrowserVersion.FIREFOX_52);
		webClient.waitForBackgroundJavaScript(20 * 1000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void testJobSummaryPage()
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		// webClient.waitForBackgroundJavaScript(5000);

		HtmlPage htmlPage = webClient.getPage(JOBSITE_URL);
		// System.out.println(htmlPage);

		// HtmlElement htmlElement = htmlPage.getBody();

		System.out.println(htmlPage);

		List<HtmlElement> htmlElementList = htmlPage.getByXPath("//a[@class='ResAts__listing-link']");
		System.out.println(htmlElementList.size());

		// System.out.println(htmlElementList.get(1).getElementsByTagName("a").get(0).getAttribute("href"));
		// // first job link

	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage jobDetailsPage = webClient.getPage("https://cloudmargin.bamboohr.com/jobs/view.php?id=44");

		HtmlElement jobDetailsElement = jobDetailsPage.getBody();
		// System.out.println(jobDetailsElement);
		try {
			System.out.println(jobDetailsElement.getOneHtmlElementByAttribute("div", "class", "col-xs-12").asText());
		} catch (ElementNotFoundException e) {
			System.out.println("Element not found " + e);
		}

	}

}
