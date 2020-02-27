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
 * Yoco job site scraper. <br>
 * URL: https://www.yoco.co.za/za/careers/
 * 
 * @author muhammad.tarek
 * @since 2019-04-02
 */
public class TestYocoHtmlUnit extends TestAbstractScrapper {
	private static final String SITE = "https://www.yoco.co.za/za/careers/";
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText());
		System.out.println(page.asXml());
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//div[@class='row _1l8dmqu']");

		System.out.println(el.size());

		for (HtmlElement tr : el) {
			System.out.println(tr.getElementsByTagName("a").get(0).asText());
			System.out.println(tr.getElementsByTagName("a").get(0).getAttribute("href"));
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://www.yoco.co.za/za/careers/1589817/";
		HtmlPage page = CLIENT.getPage(Link);

		HtmlElement spec = page.getFirstByXPath("//div[@class='col-md-7 jobListing']");
		System.out.println(spec.asText());
	}
}
