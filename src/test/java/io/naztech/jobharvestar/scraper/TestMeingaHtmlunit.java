package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;


    public class TestMeingaHtmlunit extends TestAbstractScrapper{
	private static final String SITE = "https://jobs.50skills.com/meniga/";
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
		CLIENT.waitForBackgroundJavaScript(30 * 1000);
		List<HtmlElement> jobLink = page.getBody().getByXPath("//div[@class ='styles__JobEntry-sc-1or87b6-0 elwKCY']/div/h2");
		System.out.println(jobLink.size());
		for (HtmlElement  url : jobLink) {
			System.out.println(url);	
			
		}		
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

//	@Test
//	public void testGetJobDetails() throws IOException {
//		String Link = "https://discordapp.com/jobs";
//		HtmlPage page = CLIENT.getPage(Link);
//		CLIENT.waitForBackgroundJavaScript(10 * 1000);
//		
//		HtmlElement title = page.getBody().getFirstByXPath("//h1[@class='job-detail-title pull-left']");
//		HtmlElement location = page.getBody().getFirstByXPath("//span[@class='job-detail-location-name']");
//		HtmlElement postedDate = (HtmlElement) page.getBody().getByXPath("//div[@class='col-lg-4']").get(2);
//		List<HtmlElement> spec =  page.getBody().getByXPath("//div[@class='job-detail-content']/div[2]");
//		List<HtmlElement> pre =  page.getBody().getByXPath("//div[@class='job-detail-content']/div[3]");
//		
//		System.out.println("Job Title: "+ title.asText());
//		System.out.println("Job Location: "+ location.asText());
//		System.out.println("Job Posted Date: "+ postedDate.getElementsByTagName("span").get(1).asText());
//		System.out.println("Spec: "+ spec.get(0).asText());
//		System.out.println("Pre: "+ pre.get(0).asText());
//	}
}