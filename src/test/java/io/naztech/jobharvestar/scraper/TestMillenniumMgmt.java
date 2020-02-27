package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestMillenniumMgmt {

	private String url="https://www.glassdoor.co.uk/Jobs/Millennium-Management-Investment-Firm-Jobs-E850344.htm";
	WebClient client= new WebClient(BrowserVersion.FIREFOX_52);
	
	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		client.getOptions().setTimeout(20*1000);
		client.waitForBackgroundJavaScript(3*1000);
		client.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage page= client.getPage(url);
		System.out.println(page.getUrl().toString());
		String baseUrl= url.substring(0, 27);
		System.out.println(baseUrl);
	}

}
