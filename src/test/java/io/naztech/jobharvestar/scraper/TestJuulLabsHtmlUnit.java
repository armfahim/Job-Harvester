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
 * JUUL Labs test job site parser.<br>
 * URL: https://www.juul.com/join-us
 * 
 * @author jannatul.maowa
 * @since 2019-05-02
 */
public class TestJuulLabsHtmlUnit extends TestAbstractScrapper{

	private static WebClient client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client =getFirefoxClient();	
	}

	@Test
	public void testgetScrapedJobs() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page=client.getPage("https://www.juul.com/join-us");
		client.waitForBackgroundJavaScript(30 * 1000);
		List<HtmlElement> jobListE=page.getBody().getByXPath("//a[@class='JobBoard__job-title']");
		System.out.println(jobListE.size());
	}

}
