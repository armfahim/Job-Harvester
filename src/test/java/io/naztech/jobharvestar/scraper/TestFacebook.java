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

public class TestFacebook extends TestAbstractScrapper{
	private static final String SITE = "https://www.facebook.com/careers/jobs";
	private static WebClient client = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void test() throws IOException {

	}


	@Test
	public void testDates() {
		
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(TIME_10S);

		List<HtmlElement> jobLink = page.getBody().getElementsByAttribute("a", "class", "_69jm");

		for (int i = 0; i < jobLink.size(); i++) {
			System.out.println(jobLink.get(i).getAttribute("href"));
		}
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(TIME_10S);
		
		String[] parts = page.getBody().getOneHtmlElementByAttribute("div", "class", "_3m9 _1n-z _6hy- _2pit _6b15").asText().split("of");
		int totalPage = getPageCount(parts[1].trim(), 10);
		for(int i=2; i<=totalPage; i++) {
			page = client.getPage("https://www.facebook.com" + "/careers/jobs?page=" + i);
			client.waitForBackgroundJavaScript(TIME_5S);
			
			List<String> links = prepareJobLinks(page);
			for (String it : links) {
				System.out.println(it);
			}
		}
	}
	
	private List<String> prepareJobLinks(HtmlPage page) {
		List<String> links = new ArrayList<>();
		List<HtmlElement> jobLink = page.getBody().getElementsByAttribute("a", "class", "_69jm");

		for (int i = 0; i < jobLink.size(); i++) {
			links.add(jobLink.get(i).getAttribute("href"));
		}
		return links;
	}
	

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		
		String Link = "https://www.facebook.com/careers/jobs/160446487890042/";
		
		HtmlPage desPage = client.getPage(Link);
		client.waitForBackgroundJavaScript(5 * 1000);
		
		System.out.println("Title: \n"+desPage.getBody().getOneHtmlElementByAttribute("h4", "class", "_1zbm _1kdc").asText());
		System.out.println("Location: \n"+desPage.getBody().getOneHtmlElementByAttribute("span", "class", "_3-8r _7vwo").asText());
		System.out.println("Category: \n"+desPage.getBody().getOneHtmlElementByAttribute("span", "class", "_1d1x _2t6c _5knk _2t54").asText());
		System.out.println("ApplicationUrl: \n"+desPage.getBody().getOneHtmlElementByAttribute("a", "class", "_42ft _1p05 _2t6c _5kni _3nu9 _3nua _3nub _6ad5 _6c48").getAttribute("href"));
		System.out.println("Spec: \n"+desPage.getBody().getOneHtmlElementByAttribute("div", "class", "_3m9 _1n-z _6hy- _6ad1").asText());

		List<HtmlElement> prerequisiteL = desPage.getBody().getElementsByAttribute("div", "class", "_3-8q");
		String prerequisite = prerequisiteL.get(0).asText();
		prerequisite += prerequisiteL.get(1).asText();
		System.out.println("Prerequisite: \n"+prerequisite);
		
		}
}