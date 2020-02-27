package io.naztech.jobharvestar.scraper;
import java.io.IOException;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Test Marqeta jobs site parsing using HtmlUnit.
 *  
 * @author rafayet.hossain
 * @since 2019-04-02
 */


@Slf4j
public class TestMarqetaHtmlUnit extends TestAbstractScrapper {

	String jobUrl = "https://www.marqeta.com/company/careers/all-jobs";

	String baseUrl = "https://www.marqeta.com";
	String tailUrl;
	private static WebClient client;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}
	
	

	@Test
	public void testJobSummaryPage() throws IOException {
		try {
			
			//Job job = new Job();

			HtmlPage page = client.getPage(jobUrl);
			String iframeE = page.getElementById("grnhse_iframe").getAttribute("src");
			page = client.getPage(iframeE);
			//client.waitForBackgroundJavaScript(TIME_5S);
			List<HtmlElement> jobList = page.getByXPath("//div[@class='opening']");
			//System.out.println(jobList.size());
			
			for (int i = 0; i < jobList.size(); i++) 
			{
				HtmlElement title = (HtmlElement) jobList.get(i).getByXPath("//div[@class='opening']/a").get(i);
				System.out.println(title.getTextContent());	//Title
				
				HtmlElement location = (HtmlElement) jobList.get(i).getByXPath("//span[@class='location']").get(i);
				System.out.println(location.getTextContent());
				
				System.out.println("Url:" + title.getAttribute("href"));

			}

			

		} catch (FailingHttpStatusCodeException e) {

			log.error("Error on testing Summary Page", e, e);
		}
	}

	@Test
	public void testJobDetailPage() throws IOException {

		try {
			 String jobLink = "Url:https://www.marqeta.com/company/careers/all-jobs?gh_jid=1538429";
			 
			 HtmlPage page = client.getPage(jobLink);
			 String iframeE = page.getElementById("grnhse_iframe").getAttribute("src");
			 page = client.getPage(iframeE);

			 HtmlElement description =  (HtmlElement) page.getByXPath("//div[@id='content']").get(0);

			 System.out.println(description.getTextContent());
			

		} catch (FailingHttpStatusCodeException e) {

			log.error("Error on testing Detail Page", e, e);
		}
	}

}
