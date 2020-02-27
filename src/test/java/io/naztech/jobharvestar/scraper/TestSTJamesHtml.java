package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * STJAMES job site scraper. <br>
 * URL: https://careers.sjp.co.uk/vacancies.html
 * 
 * @author Md. Sanowar Ali
 * @since 2019-05-12
 */
public class TestSTJamesHtml extends TestAbstractScrapper {
	private static WebClient client;
	private static final String SITE_URL = "https://careers.sjp.co.uk/vacancies.html";
	int pageCount;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}
	
	//Done
	@Test
	public void testgetSummaryPages() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(5 * 1000);
		List<HtmlElement> aList = page.getByXPath("//div[@class='jobpost_wrapper']/h2/a");
		for(int i=0;i<aList.size();i++) {
		System.out.println("Job URL: " + aList.get(i).getAttribute("href"));
		}
	}

	@Test
	public void testgetJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {	
	HtmlPage page = client.getPage(SITE_URL);
	client.waitForBackgroundJavaScript(5 * 1000);
	List<HtmlElement> rowList = page.getByXPath("//div[@class='jobpost_wrapper']/h2/a");
	for (int i = 0; i < rowList.size(); i++) {
		String jobURL = rowList.get(i).getAttribute("href");
		HtmlPage page2 = client.getPage(jobURL);
		client.waitForBackgroundJavaScript(10 * 1000);
		HtmlElement elTitle = page2.getBody().getFirstByXPath("//h2[@class='ptitle center']");
		HtmlElement elDepartment = page2.getBody().getFirstByXPath("//div[@id='department']");
		HtmlElement elLocation = page2.getBody().getFirstByXPath("//div[@id='location']");
		HtmlElement elType = page2.getBody().getFirstByXPath("//div[@id='type']");
		HtmlElement elSpec = page2.getFirstByXPath("//div[@class='job_summary']");
		
		System.out.println(elTitle.getTextContent().trim());
		System.out.println(elDepartment.getTextContent().trim());
		System.out.println(elLocation.getTextContent().trim());
		System.out.println(elType.getTextContent().trim());
		System.out.println(elSpec.asText().trim());
		System.out.println("...........................................................................");
		}
	}

	//Done
	@Test
	public void testTotalJob() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(5 * 1000);
		List<HtmlElement> rowList = page.getByXPath("//div[@class='jobpost_wrapper']/h2/a");
		System.out.println("Total Job: " + rowList.size());
		assertEquals(30, rowList.size());
	}
}

