package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 * prospa Jobsite Parser
 * url https://www.prospa.com/about-us/careers#open-jobs
 * @author Muhammad Bin Farook
 * @since 2019-03-27
 */
public class TestProspa extends TestAbstractScrapper  {
	private static final String JOBSITE_URL = "https://www.prospa.com/about-us/careers#open-jobs";

	private static WebClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(30 * 1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.getOptions().setThrowExceptionOnScriptError(false);

		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.setJavaScriptTimeout(30 * 1000);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		HtmlPage page = client.getPage(JOBSITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);

		List<HtmlElement> divs = page.getByXPath("//div[@class='prospa-jobs']/table[@class='prospa-jobs-listing']");
		System.out.println(divs.size());

		for (HtmlElement el : divs) {
			String category=el.getElementsByTagName("caption").get(0).asText();
			List<HtmlElement>joblink=el.getElementsByTagName("a");
			for(HtmlElement ell:joblink)
			{
			
				testJobDetails("https://www.prospa.com/about-us/"+ell.getAttribute("href"),category);	
			}
			
		}
	}

	private void testJobDetails(String link,String category)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
System.out.println(link);
		HtmlPage page = client.getPage(link);
		client.waitForBackgroundJavaScript(10 * 1000);
		HtmlElement title = page.getFirstByXPath("//section[@id='mk-page-introduce']//h1[@class='page-title ']");
		System.out.println("JOB TITLE: " + title.asText());
		HtmlElement location = page.getFirstByXPath("//div[@class='prospa-jobs']/h3[@class='job-location']");
		System.out.println("JOB LOCATION: " + location.asText());
		
         HtmlElement des=page.getFirstByXPath("//div[@class='prospa-jobs']/div[@class='job-content']");
		System.out.println("SPEC: \n" + des.asText().trim());
		
		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------");
		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------");

	}
}
