package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestHtmlUnit extends TestAbstractScrapper {

	private static WebClient client = null;
	private String baseUrl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetList()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		String url = "https://jobs.siemens-info.com/jobs?page=1";
		baseUrl = url.substring(0, 29);
		System.out.println(baseUrl);
		client = getFirefoxClient();
		HtmlPage page = client.getPage(url);
		client.waitForBackgroundJavaScript(TIME_10S * 2);
		List<HtmlElement> jobList = page.getByXPath("//a[@itemprop='url']");
		System.out.println(jobList.size());
		for(int i = 0; i < jobList.size(); i++) {
			System.out.println(jobList.get(i).asText());
		}		
	}

	@Test
	public void testDetPage()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		String url = "https://careers.unity.com/position/business-development-manager/1628282";
		client = getFirefoxClient();
		HtmlPage page = client.getPage(url);
//		client.waitForBackgroundJavaScript(20 * 1000);
		HtmlElement jobE = page.getFirstByXPath("//h1[@class='c-wh mb10']");
		System.out.println(jobE.asText());
		jobE = page.getFirstByXPath("//h3[@class='c-wh mb0']");
		System.out.println(jobE.asText());
		jobE = page.getFirstByXPath("//div[@class='bbw p20 meta-area bg-lg']/div/p");
		System.out.println(jobE.asText());
		jobE = page.getFirstByXPath("//div[@class='bbw p20 meta-type bg-lg']/div/p");
		System.out.println(jobE.asText());
		jobE = page.getFirstByXPath("//div[@class='bbw p20 meta-apply bg-lg']/div/a");
		System.out.println(jobE.getAttribute("href"));
		jobE = page.getFirstByXPath("//div[@class='info']");
		System.out.println(jobE.asText());
	}
}