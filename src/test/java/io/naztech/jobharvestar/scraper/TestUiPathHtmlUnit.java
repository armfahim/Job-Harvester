package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestUiPathHtmlUnit extends TestAbstractScrapper{
	
	private static final String SITE = "https://www.uipath.com/company/careers";
	private static WebClient webClient;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}
	
	@Test
	public void testGetJobList() throws IOException, InterruptedException {
		HtmlPage page = webClient.getPage(SITE);
		List<HtmlElement> row = page.getByXPath("//div[@class='CareersTeams-team--item']/a");
		for (HtmlElement htmlElement : row) {
			HtmlPage nextPage = webClient.getPage(htmlElement.getAttribute("href"));
			webClient.waitForBackgroundJavaScript(8 * 1000);
			HtmlInlineFrame iframe = (HtmlInlineFrame) nextPage.getElementsByTagName("iframe").get(0);
			HtmlPage innerPage = (HtmlPage) iframe.getEnclosedPage();
			List<HtmlElement> jobList = innerPage.getByXPath("//div[@class='job-list']");
			for (HtmlElement jobUrlElement : jobList) {
				System.out.println("Job URL: "+jobUrlElement.getElementsByTagName("a").get(0).getAttribute("href"));
				System.out.println("Job TITLE: "+jobUrlElement.getElementsByTagName("a").get(0).getTextContent());
				System.out.println("Job LOCATION: "+jobUrlElement.getElementsByTagName("div").get(0).getTextContent());
			}
		}
	}
	
	@Test
	public void testGetJobDetails() throws IOException {
		String link = "https://www.uipath.com/company/jobs?page=office-manager/3186/";
		HtmlPage page = webClient.getPage(link);
		webClient.waitForBackgroundJavaScript(8 * 1000);
		HtmlInlineFrame iframe = (HtmlInlineFrame) page.getElementsByTagName("iframe").get(0);
		HtmlPage innerPage = (HtmlPage) iframe.getEnclosedPage();
		HtmlElement jobDetails = innerPage.getBody().getOneHtmlElementByAttribute("div", "class", "content-section clearfix");
		System.out.println("CATEGORY: "+jobDetails.getElementsByTagName("h6").get(1).getTextContent().trim());
		System.out.println("APPLY URL: "+jobDetails.getElementsByTagName("a").get(0).getAttribute("href").trim());
		System.out.println("DESCRIPTION: "+innerPage.getBody().getOneHtmlElementByAttribute("div", "class", "body-row width_800")
				.getElementsByTagName("div").get(0).asText().trim());
	}
}
