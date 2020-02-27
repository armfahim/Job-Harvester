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

public class TestOrigami {
	private static final String SITE ="https://open.talentio.com/1/c/origami/requisitions/207";
	private WebClient client;

	@Before
	public void setUp() throws Exception {
		client = new WebClient(BrowserVersion.CHROME);
		client.getOptions().setTimeout(30*1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30*1000);
		client.waitForBackgroundJavaScript(15*1000);
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}

	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page= client.getPage(SITE);
		List<HtmlElement> jobTitleList=page.getBody().getByXPath("//div[@class='category-container']/ul/li/a");
		System.out.println(jobTitleList.size());
		for(int i=0;i<jobTitleList.size();i++)
		{
			System.out.println(jobTitleList.get(i).getAttribute("href"));
		}
	}
	@Test
	public void testLoc() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page= client.getPage("https://open.talentio.com/1/c/origami/requisitions/detail/7603");
		String spec=null;
		List<HtmlElement> jobTitleList=page.getBody().getByXPath("//div[@class='col-sm-9 col-xs-12 content']");
		for(int i=0;i<jobTitleList.size();i++)
		{
			spec=spec+jobTitleList.get(i).getTextContent();
		}
		System.out.println(spec);
	}

}
