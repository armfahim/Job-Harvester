package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.apache.http.conn.HttpHostConnectException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestToastSelenium {

	private static WebClient client=null;
	
	@BeforeClass
	public static void  beforeClass() {
		client= new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(60000);
		client.getOptions().setThrowExceptionOnScriptError(true);
		//client.getOptions().setJavaScriptEnabled(false);
	}
	@Test
	public void test() throws HttpHostConnectException {
		try {
			HtmlPage page= client.getPage("https://jobbio.com/tradeix");
			client.waitForBackgroundJavaScript(40*1000);
			System.out.println(page.asText());
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
	}

}
