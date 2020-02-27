package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestSumitomo {

	private String url="https://www.glassdoor.com/job-listing/web-developer-program-analyst-3i-people-JV_IC1155861_KO0,29_KE30,39.htm?jl=3102979823&ctt=1551780380600";
	private static final String MINOR_XPATH="//div[@class='JobDetailsInfo']/div/div/div";
	private static final String MAX_XPATH="//div[@class='minor cell alignRt']";
	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient client= new WebClient();
		client.getOptions().setTimeout(10*1000);
		client.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage page= client.getPage(url);
		System.out.println(page.asText());
		DomElement el = page.getFirstByXPath(MINOR_XPATH);
		System.out.println(el.getTextContent());
		client.close();
	}

}
