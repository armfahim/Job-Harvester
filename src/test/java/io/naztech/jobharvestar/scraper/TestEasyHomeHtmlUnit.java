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
 * Easy Home job site scraper. <br>
 * URL: https://goeasy.talentnest.com/en?page=0
 * 
 * @author a.s.m. tarek
 * @since 2019-03-12
 */
public class TestEasyHomeHtmlUnit extends TestAbstractScrapper {
	private static final String SITE = "https://goeasy.talentnest.com/en";
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//div[@class='rounded-corners posting']");
		System.out.println(el.size());

		for(int i=0; i<el.size(); i++) {

			List<HtmlElement> title = page.getByXPath("//a[@class='job-link']");
			System.out.println(title.get(i).getAttribute("href"));
			System.out.println(title.get(i).asText());
			
			List<HtmlElement> location = page.getByXPath("//div[@class='posting-cell-content location']");
			System.out.println(location.get(i).asText().split("Location")[1].trim());
		}
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText());
		System.out.println(page.asXml());
	}

	@Test
	public void testGetNextPage()
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//span[@class='hidden-phone']");
		int totalPage = Integer.parseInt(el.get(1).getLastElementChild().asText().trim());
		System.out.println(totalPage);
		for (int i = 1; i <= totalPage; i++) {

			System.out.println(SITE + "?page=" + i);
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://goeasy.talentnest.com/en/posting/44618";

		HtmlPage page = CLIENT.getPage(Link);

		List<HtmlElement> el = page.getByXPath("//div[@class='posting-header']");
		System.out.println((el.get(0).asText().split("Posted On:")[1].trim()).split("Employment")[0].trim());
		System.out.println(el.get(0).asText().split("Employment Type:")[1].trim());
		System.out.println("Job Description: "
				+ page.getBody().getOneHtmlElementByAttribute("div", "class", "field rich-text-doc").asText());
	}
}