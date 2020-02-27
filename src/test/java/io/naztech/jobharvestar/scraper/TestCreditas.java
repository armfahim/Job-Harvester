package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestCreditas extends TestAbstractScrapper {

	private static final String SITE="https://jobs.kenoby.com/creditas/";
	private static WebClient webClient;
	private static HtmlPage page;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient=getChromeClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		page=webClient.getPage(SITE);
		webClient.waitForBackgroundJavaScript(20*1000);
		List<HtmlElement> jobCategory = page.getByXPath("//div[@class='segment']");
		for (HtmlElement htmlElement : jobCategory) {
			String category = htmlElement.getElementsByTagName("h2").get(0).getTextContent().replace(".", "").trim();
			List<HtmlElement> jobUrl = htmlElement.getElementsByTagName("a");
			for (HtmlElement htmlElement2 : jobUrl) {
				System.out.println("CATEGORY: "+category);
				System.out.println("TITLE: "+htmlElement2.getTextContent().split("\n")[1].trim());
				System.out.println("LOCATION: "+htmlElement2.getElementsByTagName("span").get(0).getTextContent().trim());
				System.out.println("URL: "+htmlElement2.getAttribute("href"));
				System.out.println("=============================================================");
			}
			System.out.println("=====================CATEGORY==============================");
		}
	}
	
	@Test
	public void testGetJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String Link = "https://jobs.kenoby.com/creditas/job/product-manager/5be43b57263e1462a14315ba";
		page=webClient.getPage(Link);
		webClient.waitForBackgroundJavaScript(TIME_10S);
		HtmlElement desc = page.getBody().getOneHtmlElementByAttribute("div", "class", "description");
		System.out.println("DESCRIPTION: "+desc.asText().trim());
	}
}
