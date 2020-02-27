package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Warbyparker jobs site parse <br>
 * URL: https://www.warbyparker.com/jobs/retail
 * 
 * @author sohid.ullah
 * @since 2019-03-18
 */

public class TestWarbyParkerHTMLUnit extends TestAbstractScrapper {

	private static final String JOBSITE_URL = "https://www.warbyparker.com/jobs/retail";
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

			webClient.waitForBackgroundJavaScript(5000);
			List<HtmlElement> jobElement = page.getByXPath("//div[@class = 'u-dt u-cl u-pr u-mb18 u-pb2']");
			int numberOfJob = jobElement.size();
			// System.out.println(numberOfJob);

			for (int i = 0; i < numberOfJob; i++) {
				String url = jobElement.get(i).getElementsByTagName("a").get(0).getAttribute("href");
				HtmlPage nextPage = webClient.getPage(url);
				// System.out.println(url);
				Thread.sleep(TIME_5S);

				HtmlElement jobTitleEl = nextPage.getFirstByXPath("//div[@class = 'rake-job-details-header']/div[1]");

				String jobTitle = jobTitleEl.getTextContent();

				HtmlElement jobLocationEl = nextPage.getFirstByXPath("//div[@class = 'rake-job-location']");
				String jobLocation = jobLocationEl.getTextContent();

				HtmlElement jobDescEl = nextPage.getFirstByXPath("//div[@class = 'rake-job-description']");
				String jobDesc = jobDescEl.getTextContent();

				System.out.println(jobTitle);

			}

		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println(e.getMessage());

			// log.error("Error on testing Summary Page", e, e);
		}
	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		HtmlPage page = webClient
				.getPage("https://boards.greenhouse.getrake.io/warbyparker/jobs/1296610?gh_jid=1296610");
		webClient.waitForBackgroundJavaScript(TIME_10S);
		HtmlElement jobE = page.getFirstByXPath("//div[@class = 'rake-job-location']");
		System.out.println(jobE.asText());

		jobE = page.getFirstByXPath("//div[@class = 'rake-job-description']");
		System.out.println(jobE.asText());
	}
}
