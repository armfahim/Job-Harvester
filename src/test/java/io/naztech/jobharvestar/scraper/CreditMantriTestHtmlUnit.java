package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CreditMantriTestHtmlUnit extends TestAbstractScrapper{
	private static final String SITE = "https://www.naukri.com/credit-mantri-jobs-careers-678662";
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
		CLIENT.waitForBackgroundJavaScript(20 * 1000);
//		System.out.println(page.asText()); // Necessary Data Not Found 
//		System.out.println(page.asXml());  // Necessary Data Not Found
		List<HtmlElement> row = page.getBody().getByXPath("//span[@class='content']");
		System.out.println(row.size());
		for (HtmlElement  url : row) {
			 DomNodeList<HtmlElement> cols = url.getElementsByTagName("ul");
			 HtmlElement link = cols.get(0).getElementsByTagName("a").get(0);
			 System.out.println(link.getAttribute("href"));
			 System.out.println(link.asText());
		}
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://www.naukri.com/job-listings-Front-End-Developer-Node-js-Jobs-Hyderabad-6-to-10-years-290319901598?src=jobsearchDesk&sid=15543608183561&xp=1&px=1";
		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(10 * 1000);
		//Necessary DataFound 
	//	List<HtmlElement> location = page.getBody().getByXPath("//div[@class='loc collapsed']");
		List<HtmlElement> postedDate = page.getBody().getByXPath("//div[@class='sumFoot']/span");
		String date [] = postedDate.get(2).asText().split(" ");
		System.out.println(date[1]+" "+date[2]+" "+date[3]);
//		System.out.println("Posted Date: "+postedDate.get(2).asText());
//		HtmlElement spec =  page.getBody().getFirstByXPath("//ul[@class='listing mt10 wb']");
//		System.out.println("Job Location: "+ location.get(0).asText());
//		System.out.println("Spec: "+ spec.asText());
	}
}