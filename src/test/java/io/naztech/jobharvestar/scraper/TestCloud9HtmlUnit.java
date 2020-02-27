package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * Cloud9 job site scraper. <br>
 * URL: https://cloud9.recruiterbox.com/
 * 
 * @author muhammad tarek
 * @since 2019-03-25
 */
public class TestCloud9HtmlUnit extends TestAbstractScrapper{
	private static final String SITE = "https://cloud9.recruiterbox.com/";
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText()); // Necessary Data Not Found 
		System.out.println(page.asXml());  // Necessary Data Not Found
	}
	
	@Test
	public void testJobRow() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> rowList = page.getByXPath("//div[@class='col-xs-8 col-md-9']");
		System.out.println(rowList.size());
	}
}