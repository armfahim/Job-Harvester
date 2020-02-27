package io.naztech.jobharvestar.scraper;

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

public class TestCorpEzetap {
	private static final String SITE ="https://corp.ezetap.com/careers/";
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
		List<HtmlElement> jobTitleList=page.getBody().getByXPath("//div[@class='job-role']/h4");
		for(int i=0;i<jobTitleList.size();i++)
		{
			System.out.println(jobTitleList.get(i).getTextContent());
		}
		List<HtmlElement> jobTitleList1=page.getBody().getByXPath("//div[@class='acc-desc']");
		System.out.println(jobTitleList1.size());
		
	}
	@Test
	public void TestJodSpec() throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		HtmlPage page= client.getPage(SITE);
		List<HtmlElement> jobTitleList1=page.getBody().getByXPath("//div[@class='acc-desc']");
		for(int i=0;i<jobTitleList1.size();i++)
		{
			System.out.println("Job npo: "+(i+1));
			String spec = jobTitleList1.get(i).getElementsByTagName("ul").get(0).getTextContent();
			String prec = jobTitleList1.get(i).getElementsByTagName("ul").get(1).getTextContent();
			System.out.println("Spec: "+spec);
			System.out.println("Prec: "+prec);
			
		}
		List<HtmlElement> locationList=page.getBody().getByXPath("//div[@class='job-details']/p[2]");
		System.out.println(locationList.size());
	}

}
