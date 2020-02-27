package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author muhammad.tarek
 *
 */
public class TestSap extends TestAbstractScrapper {
	private static final String SITE = "https://jobs.sap.com/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=";
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//tr[@class='data-row clickable']");

		for (HtmlElement tr : el) {

			HtmlElement title = tr.getElementsByTagName("a").get(0);
			HtmlElement location = tr.getElementsByTagName("td").get(1);

			System.out.println(title.getAttribute("href"));
			System.out.println(location.asText());

		}
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText());
		System.out.println(page.asXml());
	}

	@Test
	public void testGetNextPage()
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//span[@class='srHelp']");
		int totalPage = Integer.parseInt(el.get(0).asText().split("of")[1].trim());
		for (int i = 0; i < totalPage; i++) {

			System.out.println(SITE + (i * 25 - 25));
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://jobs.sap.com/job/Brisbane-Technical-Senior-Consultant-SAP-Integration-&-Security-Senior-Consultant-Job-QLD/509523801/";

		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(10 * 1000);

		System.out.println("Job Posted Date: "
				+ page.getBody().getOneHtmlElementByAttribute("span", "itemprop", "datePosted").asText());
		System.out.println("Job ApplicationUrl: " + page.getBody()
				.getOneHtmlElementByAttribute("button", "class", "btn btn-primary btn-large btn-lg dropdown-toggle")
				.getAttribute("href"));
		System.out.println("Job Description: "
				+ page.getBody().getOneHtmlElementByAttribute("span", "class", "jobdescription").asText());
		System.out.println("Requisition ID: " + page.getBody()
				.getOneHtmlElementByAttribute("span", "class", "jobdescription").asText().substring(22, 28).trim());
	}
}