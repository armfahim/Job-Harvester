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
 * Gimi job site scraper. <br>
 * URL: https://gimi.teamtailor.com/
 *
 * @author Iftekar
 * @since 2019-03-31
 */
public class TestGimi extends TestAbstractScrapper {
	private static final String SITE = "https://gimi.teamtailor.com/";
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> jobList = page.getByXPath("//div[@class='job-listing-container']/ul/li");
		System.out.println(jobList.size());		
		for (HtmlElement el : jobList) {
			HtmlElement link = el.getElementsByTagName("a").get(0);
			System.out.println(link.getAttribute("href"));
			System.out.println(link.getElementsByTagName("span").get(0).asText());
		}
	}	
	

	@Test
	public void testFirstPage() throws IOException {
		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText());
		System.out.println(page.asXml());
	}
	@Test
	public void testGetJobDetails() throws IOException {
		String D_Site = "https://gimi.teamtailor.com/jobs/163945-native-english-copywriter";
		HtmlPage page = CLIENT.getPage(D_Site);
		CLIENT.waitForBackgroundJavaScript(10 * 1000);
		System.out.println("Job Description: "
						+page.getBody().getOneHtmlElementByAttribute("div", "class", "body u-margin-top--medium u-primary-text-color").asText());
		System.out.println("Job ApplicationUrl: " + page.getBody()
		.getOneHtmlElementByAttribute("a", "class", "btn btn-apply u-primary-button-text-color u-primary-button-background-color u-no-margin ")
		.getAttribute("href"));
	}
	
	
}
