package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestMicrosoftWithHtmlunit {
	private static WebClient webClient = null;
	
	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		webClient=getWebClient();
		HtmlPage page=webClient.getPage("https://careers.microsoft.com/us/en/search-results?rt=professional");
		webClient.waitForBackgroundJavaScriptStartingBefore(10*1000);
		webClient.waitForBackgroundJavaScript(10*1000);
		System.out.println(page.getTitleText());
		List<HtmlElement> jobElList= page.getByXPath("//div[@class='information']//a");
		for (HtmlElement htmlElement : jobElList) {
			System.out.println(htmlElement.getAttribute("href"));
		}
		
		
	}
	
	private WebClient getWebClient() {
		if (webClient == null) {
			webClient = new WebClient(BrowserVersion.FIREFOX_52);
			webClient.getOptions().setDoNotTrackEnabled(true);
			webClient.getOptions().setMaxInMemory(0);
			webClient.getOptions().setTimeout(30 * 1000);
			webClient.getOptions().setCssEnabled(false);
		}
		return webClient;
	}

}
