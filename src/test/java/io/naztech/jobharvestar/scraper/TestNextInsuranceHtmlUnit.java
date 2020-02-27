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
 * Test Next Insurance jobs site parsing using HtmlUnit.
 *  
 * @author rafayet.hossain
 * @since 2019-04-02
 */


@Slf4j
public class TestNextInsuranceHtmlUnit extends TestAbstractScrapper {

	String jobUrl = "https://www.next-insurance.com/careers/";

	String baseUrl = "https://www.next-insurance.com";
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
			//client.waitForBackgroundJavaScript(TIME_5S);
			List<HtmlElement> jobList = page.getByXPath("//div[@class='position']");
			for (int i = 0; i < jobList.size(); i++) 
			{
				HtmlElement title = (HtmlElement) jobList.get(i).getByXPath("//div[@class='position']/h2/a").get(i);
				System.out.println(title.getTextContent());	//Title
				
				HtmlElement location = (HtmlElement) jobList.get(i).getByXPath("//a[@class='category location']").get(i);
				System.out.println(location.getTextContent());
				HtmlElement catagory =  (HtmlElement) jobList.get(i).getByXPath("//a[@class='category department']").get(i);
				System.out.println(catagory.getTextContent());
				
				System.out.println("JobUrl:" + title.getAttribute("href"));
				
//				HtmlElement applicationUrl = (HtmlElement) jobList.get(i).getByXPath("//a[@class='btn btn-primary-ghost']").get(i);
//				
//				System.out.println(baseUrl + applicationUrl.getAttribute("href"));
//				
//				
//				job.setTitle(title.getTextContent());
//				job.setName(job.getTitle());
//				job.setLocation(location.getTextContent());
//				job.setApplicationUrl(baseUrl + applicationUrl.getAttribute("href"));
//				job.setUrl(job.getApplicationUrl());
			}

			

		} catch (FailingHttpStatusCodeException e) {

			log.error("Error on testing Summary Page", e, e);
		}
	}

	@Test
	public void testJobDetailPage() throws IOException {

		try {
			 String jobLink = "https://www.next-insurance.com/careers/full-stack-web-developer/";
			 
			 HtmlPage page = client.getPage(jobLink);
			 HtmlElement description =  (HtmlElement) page.getByXPath("//div[@class='position-content']").get(0);

			 System.out.println(description.getTextContent());
			

		} catch (FailingHttpStatusCodeException e) {

			log.error("Error on testing Detail Page", e, e);
		}
	}

}
