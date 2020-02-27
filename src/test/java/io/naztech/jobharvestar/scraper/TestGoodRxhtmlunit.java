package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 * @name GoodRx jobsite
 * @author Muhammad Bin Farook
 * @since 2019-03-12
 */

public class TestGoodRxhtmlunit extends TestAbstractScrapper{
	private static final String JOBSITE_URL = "https://www.goodrx.com/jobs";
	
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
	public void testJobDetailElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		
		HtmlPage page = client.getPage(JOBSITE_URL);
		client.waitForBackgroundJavaScript(20*1000);
		HtmlInlineFrame iframe = (HtmlInlineFrame) page.getElementsByTagName("iframe").get(0);
		HtmlPage innerPage = (HtmlPage) iframe.getEnclosedPage();
		
		List<HtmlElement> el = innerPage.getBody().getElementsByTagName("a");
		List<String> joblink=new ArrayList<>();
		for(int i=0;i<el.size();i++)
		{
			joblink.add(el.get(i).getAttribute("href"));
			
		}
		
		for(int i=0;i<joblink.size();i++)
		{
			page=client.getPage(joblink.get(i));
			System.out.println("JOB TITLE: "+page.getElementsByTagName("h1").get(0).asText());
			List<HtmlElement> jtype= page.getByXPath("//li[@class='bb-jobs-posting__job-details-item ptor-job-view-department']");
			System.out.println("JOB TYPE: "+jtype.get(0).asText());
			List<HtmlElement> jloc= page.getByXPath("//li[@class='bb-jobs-posting__job-details-item ptor-job-view-location']");
			System.out.println("JOB Location: "+jloc.get(0).asText());
			List<HtmlElement> divs= page.getByXPath("//div[@class='bb-jobs-posting__content']//div[@class='bb-rich-text-editor__content ptor-job-view-description public-job-description']/*");
			
			System.out.println("JOB DESCRIPTION: ");
			System.out.println("---------------");
			System.out.println("---------------");
			String str = null;
			for(int j=0;j<divs.size()-2;j++) {
			
				str+=divs.get(j).asText()+"\n";
			}
			System.out.println(str);
			System.out.println();
			
		}
	}
	

}
