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
 * DoorDash job site scraper. <br>
 * URL: https://www.doordash.com/careers/
 *
 * @author a.s.m. tarek
 * @since 2019-03-12
 */
public class TestFundBoxHtmlUnit extends TestAbstractScrapper {
	private static final String SITE = "https://fundbox.com/careers/";
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
		List<HtmlElement> el = page.getByXPath("//div[@class='jobs-data-container sf']/div/div");
		
		System.out.println(el.size());

		for (HtmlElement tr : el) {

			HtmlElement title = tr.getElementsByTagName("li").get(0).getElementsByTagName("a").get(0);
			System.out.println(title.asText());
			System.out.println(title.getAttribute("herf"));

		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://fundbox.com/job-sf?job_id=1040095";
		HtmlPage page = CLIENT.getPage(Link);
		
		List<HtmlElement> spec = page.getByXPath("//div[@class='jobs-data-description']");
		System.out.println(spec.get(0).asText());
	}
}
