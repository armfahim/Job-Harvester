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
 * Taxify website parse <br>
 * URL: https://careers.bolt.eu/positions/
 * 
 * @author sohid.ullah
 * @since 2019-03-19
 */

public class TestTaxifyHTMLUnit extends TestAbstractScrapper{

	private static final String BASE_URL = "https://careers.bolt.eu/positions";
	private static HtmlPage page;
	private static WebClient webClient;
	
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
			page = webClient.getPage(BASE_URL);
			
			webClient.waitForBackgroundJavaScript(40000);
			List<HtmlElement> jobElement = page.getByXPath("//li[@class = 'my-30']");
			int numberOfJob = jobElement.size();
			 System.out.println(numberOfJob);
			 System.out.println(BASE_URL+ " Finished!");

		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		HtmlPage page = webClient.getPage("https://boards.greenhouse.getrake.io/warbyparker/jobs/1296610?gh_jid=1296610");
		webClient.waitForBackgroundJavaScript(TIME_10S);
		HtmlElement jobE = page.getFirstByXPath("//div[@class = 'rake-job-location']");
		System.out.println(jobE.asText());

		jobE = page.getFirstByXPath("//div[@class = 'rake-job-description']");
		System.out.println(jobE.asText());
	}
}
