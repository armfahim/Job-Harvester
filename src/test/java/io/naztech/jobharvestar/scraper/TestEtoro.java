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
 * Test Etoro jobs site parsing using htmlUnit.
 * https://www.etoro.com/about/careers/#main_jobs_content
 * 
 * @author Rahat Ahmad
 * @since 2019-04-02
 */

public class TestEtoro extends TestAbstractScrapper {

	private static String URL = "https://www.etoro.com/about/careers/#main_jobs_content";
	private static WebClient client = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void getJobList()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(URL);
		List<HtmlElement> jobListE = page.getByXPath("//div[@class='job']");
		for (HtmlElement htmlElement : jobListE) {
			page = htmlElement.getElementsByTagName("a").get(0).click();
			Thread.sleep(TIME_1S * 2);
		}

		System.out.println(jobListE.size());
	}

	@Test
	public void getDetailsPage() throws IOException, InterruptedException {
		HtmlPage page = client.getPage(URL);
		List<HtmlElement> jobListE = page.getByXPath("//div[@class='job']");
		for (int i = 0; i < jobListE.size(); i++) {
			String title = jobListE.get(i).getElementsByTagName("div").get(0).getTextContent();
			System.out.println(title);
			String category = jobListE.get(i).getElementsByTagName("div").get(1).getTextContent();
			System.out.println(category);
			page = jobListE.get(i).getElementsByTagName("a").get(0).click();
			Thread.sleep(TIME_1S * 2);
			List<HtmlElement>jobDetailE = page.getByXPath("//div[@class='job open']");
			testDetail(jobDetailE);
			
		}

	}
	
	private void testDetail(List<HtmlElement>jobDetailE) {
		
		String spec = jobDetailE.get(jobDetailE.size()-1).getOneHtmlElementByAttribute("div", "class", "job_desc").getTextContent();
		System.out.println(spec);
		String location = jobDetailE.get(jobDetailE.size()-1).getOneHtmlElementByAttribute("div", "class", "job_location col-sm-4")
				.getTextContent().replace("location", "").trim();
		System.out.println(location);
		String applicationUrl = jobDetailE.get(jobDetailE.size()-1).getOneHtmlElementByAttribute("a", "class", "send_resume")
				.getAttribute("href");
		System.out.println(applicationUrl);
	}

}
