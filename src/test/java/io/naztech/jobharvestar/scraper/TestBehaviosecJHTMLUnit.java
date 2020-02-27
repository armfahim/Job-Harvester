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
 * URL: https://www.behaviosec.com/careers/
 * 
 * @author sohid.ullah
 * @since 2019.03.27
 * 
 **/
public class TestBehaviosecJHTMLUnit {
	private static final String JOBSITE_URL = "https://www.behaviosec.com/careers/";
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
		// System.out.println(JOBSITE_URL.substring(0, 28));
		// webClient.waitForBackgroundJavaScript(5000);

		HtmlPage htmlPage = webClient.getPage(JOBSITE_URL);
		// System.out.println(htmlPage);

		HtmlElement htmlElement = htmlPage.getBody();
		// System.out.println(htmlElement);

		List<HtmlElement> htmlElementList = htmlElement.getElementsByTagName("a");
		//System.out.println(htmlElementList.size());

		for (int i = 0; i < htmlElementList.size(); i++) {
			if (htmlElementList.get(i).getAttribute("href").contains("www.behaviosec.com/job/"))
				System.out.println(htmlElementList.get(i).getAttribute("href"));
		}

	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
//		HtmlPage jobDetailsPage = webClient
//				.getPage("https://www.behaviosec.com/job/senior-sales-engineer-solution-architect/");
//
//		HtmlElement jobDetailsElement = jobDetailsPage.getBody();
//		// System.out.println(jobDetailsElement);

	//	HtmlElement jobDetailsEl = jobDetailsElement.getOneHtmlElementByAttribute("div", "class", "blog_content");
//	
//		String jobTitle = jobDetailsEl.getElementsByTagName("h2").get(0).asText().trim();
//		String jobLocation = jobDetailsEl.getElementsByTagName("h2").get(1).asText().trim();
//		String jobDesc = jobDetailsEl.asText();
		
	//	System.out.println(jobLocation.substring(9));
		
		//System.out.println(jobDesc);
		
		//String applyEmail = jobDetailsEl.getElementsByTagName("a").get(3).getAttribute("href").substring(7);
		

	}

}
