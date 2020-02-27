package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * Test Symphony jobs site parsing using HtmlUnit.
 *  
 * @author rafayet.hossain
 * @since 2019-03-19
 */


@Slf4j
public class TestSymphonyHtmlUnit extends TestAbstractScrapper {

	String jobUrl = "https://symphony.com/company/careers#jobs";

	String baseUrl = "https://symphony.com";
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
			
			Job job = new Job();

			HtmlPage page = client.getPage(jobUrl);
			client.waitForBackgroundJavaScript(TIME_5S);
			List<HtmlElement> jobList = page.getByXPath("//div[@class='row sym-careers-job']");
			for (int i = 0; i < jobList.size(); i++) 
			{
				HtmlElement title = (HtmlElement) jobList.get(i).getByXPath("//div[@class='col col-md-9 col-lg-9']/h4").get(i);
				//System.out.println(title.getTextContent());	//Title
				
				HtmlElement location = (HtmlElement) jobList.get(i).getByXPath("//div[@class='col col-md-9 col-lg-9']/p").get(i);
				//System.out.println(location.getTextContent());
				
				
				HtmlElement applicationUrl = (HtmlElement) jobList.get(i).getByXPath("//a[@class='btn btn-primary-ghost']").get(i);
				
				System.out.println(baseUrl + applicationUrl.getAttribute("href"));
				
				
				job.setTitle(title.getTextContent());
				job.setName(job.getTitle());
				job.setLocation(location.getTextContent());
				job.setApplicationUrl(baseUrl + applicationUrl.getAttribute("href"));
				job.setUrl(job.getApplicationUrl());
			}

			

		} catch (FailingHttpStatusCodeException e) {

			log.error("Error on testing Summary Page", e, e);
		}
	}

	@Test
	public void testJobDetailPage() throws IOException {

		try {
			 String jobLink = "https://symphony.com/company/apply/1569818-senior-user-experience-designer/?gh_jid=1569818";
			 
			 HtmlPage page = client.getPage(jobLink);
			 String iframeE = page.getElementById("grnhse_iframe").getAttribute("src");
			 page = client.getPage(iframeE);
			 DomElement jobE = page.getElementById("content");
			 System.out.println(jobE.asText());

			

		} catch (FailingHttpStatusCodeException e) {

			log.error("Error on testing Detail Page", e, e);
		}
	}

}
