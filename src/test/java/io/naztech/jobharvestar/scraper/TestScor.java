package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestScor {
	private static WebClient client;
	private static final String SITE_URL = "https://careers.scor.com/search/?q=";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(30*1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30*1000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

	@Test
	public void TestJobPerPage() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			List<HtmlElement> jobList = page.getByXPath("//div[@class='searchResultsShell']/table/tbody/tr");
			System.out.println(jobList.size());
		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println("Connection Failed");
		}
	}
	
	@Test
	public void TestFirstJobUrl() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			List<HtmlElement> jobUrl = page.getByXPath("//div[@class='searchResultsShell']/table/tbody/tr/td");
			System.out.println("https://careers.scor.com"+jobUrl.get(0).getElementsByTagName("a").get(0).getAttribute("href"));
		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println("Connection Failed");
		}
	}
	

}
