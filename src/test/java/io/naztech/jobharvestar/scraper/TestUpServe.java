package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestUpServe {
	private static final String SITE ="https://upserve.com/company/careers/";
	private WebClient client;

	@Before
	public void setUp() throws Exception {
		client = new WebClient(BrowserVersion.CHROME);
		client.getOptions().setTimeout(30*1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30*1000);
		client.waitForBackgroundJavaScript(10*1000);
	}
	@After
	public void tearDown() throws Exception {
		client.close();
	}

	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page= client.getPage(SITE);
		List<HtmlElement> jobTitleList=page.getBody().getByXPath("//a[@class='job-title']");
		for(int i=0;i<jobTitleList.size();i++)
		{
			System.out.println(jobTitleList.get(i).getTextContent());
			System.out.println(jobTitleList.get(i).getAttribute("href"));
		}
		
		
	}
}
