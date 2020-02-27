package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class NewDadaHtmlunit extends TestAbstractScrapper{
	private static final String SITE = "https://www.imdada.cn/job";
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
//		System.out.println(page.asText()); // Necessary Data Not Found 
//		System.out.println(page.asXml());  // Necessary Data Not Found
		CLIENT.waitForBackgroundJavaScript(20 * 1000);
		List<HtmlElement> jobLink = page.getBody().getByXPath("//div[@class='job-list']/a");
		System.out.println(jobLink.size());
		for (HtmlElement  url : jobLink) {
			System.out.println(url.getAttribute("href"));	
		}		
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://www.imdada.cn/job/detail?id=510265767";
		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(10 * 1000);
		//Necessary DataFound 
		HtmlElement title = page.getBody().getFirstByXPath("//h1[@class='job-detail-title pull-left']");
		HtmlElement location = page.getBody().getFirstByXPath("//span[@class='job-detail-location-name']");
		HtmlElement postedDate = (HtmlElement) page.getBody().getByXPath("//div[@class='col-lg-4']").get(2);
		List<HtmlElement> spec =  page.getBody().getByXPath("//div[@class='job-detail-content']/div[2]");
		List<HtmlElement> pre =  page.getBody().getByXPath("//div[@class='job-detail-content']/div[3]");
		System.out.println("Job Title: "+ title.asText());
		System.out.println("Job Location: "+ location.asText());
		System.out.println("Job Posted Date: "+ postedDate.getElementsByTagName("span").get(1).asText());
		System.out.println("Spec: "+ spec.get(0).asText());
		System.out.println("Pre: "+ pre.get(0).asText());
	}
}