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

public class TestBima {
	
	private static final String JOB_SITE_URL = "http://www.bimamobile.com/why-bima/vacancies/";
	private static final String JOB_LIST = "//*[@id=\"main\"]/section[3]/div/div/div/p/a" ;
	private static WebClient client;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(30 * 1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30 * 1000);
	}


	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

	@Test
	public void testTotalJob ()  throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(JOB_SITE_URL);
		List<HtmlElement> JobList = page.getBody().getByXPath(JOB_LIST);
		System.out.println(JobList.size());
	}
	
	@Test
	public void testDetails()  throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(JOB_SITE_URL);
		List<HtmlElement> JobList = page.getBody().getByXPath(JOB_LIST);
		for(HtmlElement title : JobList) {System.out.println(title.getAttribute("href"));}
		
	}

}
