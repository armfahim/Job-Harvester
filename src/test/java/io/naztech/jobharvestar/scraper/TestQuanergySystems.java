package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestQuanergySystems extends TestAbstractScrapper{
	private static final String SITE = "https://quanergy.recruiterbox.com";
	private static WebClient CLIENT = null;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void test() throws IOException {

	}


	@Test
	public void testDates() {
		
	}

	@Test
	public void testGetJobList() {
		
	}

	@Test
	public void testFirstPage() throws IOException {
		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);

		
		List<HtmlElement> allJobLink = page.getBody().getElementsByAttribute("a", "class", "card-title");
		List<HtmlElement> allLocation= page.getBody().getElementsByAttribute("div", "class", "card-info cut-text");
		List<HtmlElement> allType= page.getBody().getElementsByAttribute("small", "class", "badge-opening-meta");
		
		for(int i=0; i<allJobLink.size(); i++) {
			System.out.println(SITE+allJobLink.get(i).getAttribute("href"));
			System.out.println(allLocation.get(i).getAttribute("data-original-title"));
			System.out.println(allType.get(i).asText());
		}
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://quanergy.recruiterbox.com/jobs/fk0j7n1/";

		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);

		System.out.println("Job Title: "+ page.getBody().getOneHtmlElementByAttribute("h1", "class", "jobtitle meta-job-detail-title").asText());
		System.out.println("Job Description: "+ page.getBody().getOneHtmlElementByAttribute("div", "class", "jobdesciption").asText());
		System.out.println("Apply Url: "+page.getBody().getOneHtmlElementByAttribute("a", "class", "btn btn-primary btn-lg btn-apply hidden-print").getAttribute("href"));
	}


}
