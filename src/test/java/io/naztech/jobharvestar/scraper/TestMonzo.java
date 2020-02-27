package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * monzo job site scraper. <br>
 * URL: https://monzo.com/careers/#jobs
 * 
 * @author Asadullah Galib
 * @since 2019-03-12
 */

public class TestMonzo extends TestAbstractScrapper {
	private static final String SITE = "https://monzo.com/careers/#jobs";
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//div[@class='grid-row margin-top-none']/a");
		System.out.println(el.size());

		for (HtmlElement tr : el) {

			HtmlElement title = tr.getElementsByTagName("span").get(0);
			HtmlElement type = tr.getElementsByTagName("span").get(1);
			String url =tr.getAttribute("href");

			System.out.println(title.asText());
			System.out.println(type.asText());
			System.out.println(url);

		}
	}
	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://boards.greenhouse.io/monzo/jobs/1584126";
		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(10 * 1000);
		HtmlElement spec=(HtmlElement) page.getElementById("content");
		System.out.println(spec.asText());

		}
}
