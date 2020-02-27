package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Alphaflow jobsite parse <br>
 * URL: https://www.alphaflow.com/careers
 * 
 * @author sohid.ullah
 * @since 2019-03-28
 * 
 **/
public class TestAlphaflowHTMLUnit {
	private static final String JOBSITE_URL = "https://www.alphaflow.com/careers";
	private static WebClient webClient = null;

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
	public void testJobSummaryPage()
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage htmlPage = webClient.getPage(JOBSITE_URL);
		DomElement iframeE = htmlPage.getElementById("hire_widget_iframe_0");
		htmlPage = webClient.getPage(iframeE.getAttribute("src"));

		/*
		 * HtmlElement htmlElement = htmlPage.getBody();
		 * 
		 * List<HtmlElement> elementList = htmlElement
		 * .getByXPath("//li[@class='bb-public-jobs-list__job-item ptor-jobs-list__item']"
		 * ); // All Job element int totalJob = elementList.size(); // Total 6 jobs
		 * 
		 * String jobUrl =
		 * elementList.get(0).getElementsByTagName("a").get(0).getAttribute("href"); //
		 * first job link
		 * 
		 * String jobTitle =
		 * elementList.get(0).getElementsByTagName("a").get(0).asText(); // First job
		 * title
		 */
	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
	//	HtmlPage jobDetailsPage = webClient
//				.getPage("https://hire.withgoogle.com/public/jobs/alphaflowcom/view/P_AAAAAAEAAHUIN3c0uGLRye");

		//HtmlElement jobDetailsElement = jobDetailsPage.getBody();
		// System.out.println(jobDetailsElement);

		/*
		 * HtmlElement headerElement =
		 * jobDetailsElement.getOneHtmlElementByAttribute("div", "class",
		 * "bb-jobs-posting__header"); String jobTitle =
		 * headerElement.getElementsByTagName("h1").get(0).asText();
		 * 
		 * String jobcategory =
		 * headerElement.getElementsByTagName("li").get(0).asText(); String jobLocation
		 * = headerElement.getElementsByTagName("li").get(1).asText();
		 * 
		 * String j obSpecification =
		 * jobDetailsElement.getOneHtmlElementByAttribute("div", "class",
		 * "bb-rich-text-editor__content ptor-job-view-description public-job-description"
		 * ).asText();
		 * 
		 * Sy
//		 */

	}

}
