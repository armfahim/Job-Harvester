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

public class TestGrabHtmlunit {
	private static WebClient webClient = null;

	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		webClient = getWebClient();
		HtmlPage page = webClient.getPage("https://grab.careers/jobs/");
		webClient.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
		webClient.waitForBackgroundJavaScript(10 * 1000);
		System.out.println(page.getTitleText());
		List<HtmlElement> jobElList = page.getByXPath("//tbody[@class='content jobs-list']/tr");
		for (HtmlElement htmlElement : jobElList) {
			System.out.println(htmlElement.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0)
					.getAttribute("href"));
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
