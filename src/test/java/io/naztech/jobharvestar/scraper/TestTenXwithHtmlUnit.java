package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Test UNIHTC jobs site parsing using htmlunit.
 * @author Kayumuzzaman Robin
 * @since 2019-03-18
 */

public class TestTenXwithHtmlUnit extends TestAbstractScrapper {
	private static final String SITE = "https://careers.ten-x.com/";
	private static WebClient client = null;
	int totalJob = 0;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(TIME_10S);
//		System.out.println(page.asText());
		List<HtmlElement> jobLinks = page.getByXPath("//a[@class='au-target category-1 list-item-link']");
		List<String> links = new ArrayList<String>();
		System.out.println(jobLinks);
		for (HtmlElement el : jobLinks) {
			links.add(el.getAttribute("href"));
		}
		System.out.println(links);
	}
}
