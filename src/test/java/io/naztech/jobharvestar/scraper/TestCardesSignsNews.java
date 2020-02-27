package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
//import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestCardesSignsNews {
	   private static final String site_url ="https://cardesignnews.com/careers/xpeng-motors/2017/04/various-positions";
	   private static WebClient client;
	   List<String>titleList=new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		client = new WebClient(BrowserVersion.CHROME);
		client.getOptions().setTimeout(30*1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30*1000);
	}

	@Test
	public void test() throws IOException {
		HtmlPage page= client.getPage(site_url);
//		List<String>titleList=new ArrayList<>(); 
		List<HtmlElement> titles=page.getBody().getByXPath("//p/strong");
		int i;
		int count = 1;
		//String title = null;
		for(i=4;i<titles.size()-1;i++) {
			String jobTitle=titles.get(i).asText().replace(count+".", "");
			titleList.add(jobTitle);
			count++;
		}
		for(i=0;i<titleList.size();i++) {
//			String jobTitle=titles.get(i).asText().replace(count+".", "");
//			titleList.add(jobTitle);
//			count++;
			System.out.println(titleList.get(i));
		}			
		
	}
	@Test
	public void dep() throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		HtmlPage page= client.getPage(site_url);
		List<HtmlElement> titles=page.getBody().getByXPath("//div[@class='col-md-7 col-sm-8 content']/ol");
		List<String>specList=new ArrayList<>();
			for(int j=0;j<titles.size();j+=2) {
			String spec=titles.get(j).getTextContent()+titles.get(j+1).getTextContent();
			System.out.println(spec);
			specList.add(spec);
			
			
		}
			
	}
	
	@After
	public void after() {
		client.close();
	}

}
