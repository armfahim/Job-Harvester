package io.naztech.jobharvestar.scraper;

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
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 * @name BillCom jobsite
 * @URL  "https://www.bill.com/about-us/careers/jobs/"
 * @author Muhammad Bin Farook
 * @since 2019-03-24
 */
public class TestBillCom extends TestAbstractScrapper {
private static final String JOBSITE_URL = "https://www.bill.com/about-us/careers/jobs/";

	
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
		HtmlInlineFrame iframe = (HtmlInlineFrame) page.getElementById("jv_careersite_iframe_id");
		HtmlPage innerPage = (HtmlPage) iframe.getEnclosedPage();
		//System.out.println(innerPage.asText());
		
		List<HtmlElement> el = innerPage.getByXPath("//div[@class='jv-container']//a");
		System.out.println(el.size());
		List<String> joblink=new ArrayList<>();
		for(int i=0;i<el.size();i++)
		{
			if(el.get(i).getAttribute("href").contains("/bill/job/"))
			joblink.add("https://jobs.jobvite.com"+el.get(i).getAttribute("href").toString());
			//System.out.println(el.get(i).getAttribute("href").toString());}
			
		}
		
		for(int i=0;i<joblink.size();i++)
		{
			innerPage=client.getPage(joblink.get(i));
			HtmlInlineFrame iframe1 = (HtmlInlineFrame) innerPage.getElementById("jv_careersite_iframe_id");
			HtmlPage page1 = (HtmlPage) iframe1.getEnclosedPage();
			HtmlElement title=(HtmlElement) page1.getByXPath("//div[@class='jv-wrapper']//h2[@class='jv-header']").get(0);
			System.out.println("JOB TITLE: "+title.asText());
			HtmlElement url=(HtmlElement) page1.getByXPath("//div[@class='jv-job-detail-top-actions']//a[@class='jv-button jv-button-primary jv-button-apply']").get(0);
			System.out.println("APPLICATION URL: "+"https://jobs.jobvite.com"+url.getAttribute("href"));
			HtmlElement li= page1.getFirstByXPath("//p[@class='jv-job-detail-meta']");
			String str=li.getTextContent().trim();
			String[] str1=str.split("\n");
			//System.out.println(str.trim());
			String location="";
			String category=str1[0].trim();
			
			for(int j=1;j<str1.length;j++)
			{
				location+=str1[j].trim();
			}
			System.out.println("LOCATION: "+location);
			System.out.println("CATEGORY: "+category);
			
			HtmlElement des=page1.getFirstByXPath("//div[@class='jv-job-detail-description']");
			System.out.println("DESCRIPTION: \n"+des.asText().trim());
			
			
		}
	}
}
