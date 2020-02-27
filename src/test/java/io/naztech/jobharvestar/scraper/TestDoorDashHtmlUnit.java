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
 * DoorDash job site scraper. <br>
 * URL: https://www.doordash.com/careers/
 *
 * @author a.s.m. tarek
 * @since 2019-03-12
 */
public class TestDoorDashHtmlUnit extends TestAbstractScrapper {
	private static final String SITE = "https://www.doordash.com/careers/";
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//a[@class='sc-jdeSqf aAMgj Button_root___1Fnwf']");

		for (HtmlElement tr : el) {

			HtmlElement title = tr.getElementsByTagName("span").get(0);
			HtmlElement location = tr.getElementsByTagName("span").get(1);

			System.out.println(title.asText());
			System.out.println(location.asText());

		}
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText());
		System.out.println(page.asXml());
		
		List<HtmlElement> el = page.getByXPath("//p[@class='sc-cugefK dJijCY sc-gVLVqr emiNhg']");
		System.out.println(el.get(0).asText());
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
