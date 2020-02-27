package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * URL: https://brighte.com.au/careers/
 * @author sohid.ullah
 * @since 25.03.19
 * 
 * **/

public class TestBrighteHTMLUnit extends TestAbstractScrapper {

	private static final String JOBSITE_URL = "https://brighte.com.au/careers/";
	private static HtmlPage page;
	private static WebClient webClient = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void testJobSummaryPage() throws InterruptedException {
		try {
			page = webClient.getPage(JOBSITE_URL);

			webClient.waitForBackgroundJavaScript(20 * 1000);
			List<HtmlElement> jobElement = page.getByXPath("//div[@class = 'cell m-bottom-30']");
			int numberOfJob = jobElement.size();
			System.out.println("Total JOb: " + numberOfJob);

			for (int i = 0; i < numberOfJob; i++) {
				String url = jobElement.get(i).getElementsByTagName("a").get(0).getAttribute("href");
				String title = jobElement.get(i).getElementsByTagName("a").get(0).getTextContent().trim();
				//HtmlPage nextPage = webClient.getPage(url);
				System.out.println(url);
				System.out.println(title);

			}

		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println(e.getMessage());

			// log.error("Error on testing Summary Page", e, e);
		}
	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		HtmlPage page = webClient.getPage("https://brighte.com.au/careers/test-automation-engineer/");
		webClient.waitForBackgroundJavaScript(TIME_10S);
		HtmlElement jobE = page.getFirstByXPath("//div[@id = 'content']");
		System.out.println(jobE.asText());
	}
}