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
 * Policy Bazaar job site scraper. <br>
 * URL: https://www.policybazaar.com/careers/
 * 
 * @author a.s.m. tarek
 * @since 2019-03-13
 */
public class TestPolicyBazaarHtmlUnit extends TestAbstractScrapper {
	private static final String SITE = "https://www.policybazaar.com/careers/";
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//div[@class='openingsBlock']");
		System.out.println(el.size());

		for(HtmlElement tr: el) {
			
			List<HtmlElement> title = tr.getElementsByTagName("label");
			List<HtmlElement> des = tr.getElementsByTagName("p");
			List<HtmlElement> req = tr.getElementsByTagName("span");
			List<HtmlElement> spec = tr.getByXPath("//div[@class='posInfo fLeft fullWidth']");
			System.out.println(title.get(0).asText());
			System.out.println(des.get(0).asText());
			System.out.println(req.get(0).asText());
			System.out.println(spec.get(0).getTextContent());
		}
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText());
		System.out.println(page.asXml());
	}

}