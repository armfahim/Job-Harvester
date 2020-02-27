package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestUnicreditRussia extends TestAbstractScrapper {
	private static final String SITE = "https://hh.ru/search/vacancy?text=unicredit&area=1";
	private static WebClient client = null;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
		client.getOptions().setJavaScriptEnabled(false);
	}

	@Test
	public void test() throws IOException {

	}

	@Test
	public void testDates() {

	}

	@Test
	public void testGetJobList() {

	}

	@Test
	public void testFirstPage() throws IOException {
		HtmlPage page = client.getPage(SITE);
		String[] parts = page.getBody().getOneHtmlElementByAttribute("h1", "class", "header").asText().split(" ");
		
		System.out.println("totalPage: "+getPageCount(parts[0].trim(), 20));
		
		List<HtmlElement> rows = page.getByXPath("//a[@class = 'bloko-link HH-LinkModifier']");
		for (HtmlElement row : rows) {
			String jobUrl = row.getAttribute("href");
			System.out.println(jobUrl);
		}

	}


	@Test
	public void testGetJobDetails() throws IOException {
		String link = "https://hh.ru/vacancy/30628990?query=unicredit";
		HtmlPage page = client.getPage(link);

		System.out.println("jobLocation: "+page.getBody().getOneHtmlElementByAttribute("span", "data-qa", "vacancy-view-raw-address").asText());
		System.out.println("Apply Url: "+page.getBody().getOneHtmlElementByAttribute("a", "data-qa", "vacancy-response-link-top").asText());
		System.out.println("jobPreReq: "+page.getBody().getOneHtmlElementByAttribute("span", "data-qa", "vacancy-experience").asText());
		System.out.println("jobType: "+page.getBody().getOneHtmlElementByAttribute("p", "data-qa",
				"vacancy-view-employment-mode").asText());
		System.out.println("jobDes: "+page.getBody().getOneHtmlElementByAttribute("div", "class", "wrap_hh_content").asText().trim());

	}
}