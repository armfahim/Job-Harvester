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
 * Test RiskAlyze jobsite Parser in HtmlUnit<br>
 * URL: https://www.riskalyze.com/careers
 * 
 * @author jannatul.maowa
 * @since 2019-04-28
 */
public class TestRiskAlyzeHtmlUnit extends TestAbstractScrapper {
	private static WebClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void testGetScrapedJobs() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = client.getPage("https://www.riskalyze.com/careers");
		List<HtmlElement> jobList = page.getBody().getByXPath("//h3[@class='whr-title']//a");
		for (int i = 0; i < jobList.size(); i++)
			System.out.println(jobList.get(i).getAttribute("href"));
	}
}
