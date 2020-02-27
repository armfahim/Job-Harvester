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

public class TestForter extends TestAbstractScrapper {
	
	private static final String SITE="https://www.forter.com/careers/#open-positions";
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
		webClient.waitForBackgroundJavaScript(TIME_10S);
		List<HtmlElement> jobUrl = page.getByXPath("//table[@id='available-positions-table']/tbody/tr");
		for (HtmlElement htmlElement : jobUrl) {
			System.out.println("URL: "+htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
		}
	}
	
	@Test
	public void testGetJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String Link = "https://www.comeet.co/jobs/Forter/B3.007/director-of-strategic-sales/BF.D0D";
		page=webClient.getPage(Link);
		webClient.waitForBackgroundJavaScript(TIME_10S);
		HtmlElement title = page.getBody().getOneHtmlElementByAttribute("h2", "class", "positionName");
		List<HtmlElement> deptLoc = page.getByXPath("//ul[@class='positionDetails']/li");
		HtmlElement desc = page.getBody().getOneHtmlElementByAttribute("div", "class", "col-lg-6 col-md-12 col-sm-12 positionInfo");
		
		for (HtmlElement htmlElement : deptLoc) {
			if (htmlElement.getAttribute("ng-if").equalsIgnoreCase("position.department")) {
				System.out.println("CATEGORY: "+htmlElement.getTextContent().trim());
			} else if (htmlElement.getAttribute("ng-if").equalsIgnoreCase("position.location")) {
				System.out.println("LOCATION: "+htmlElement.getTextContent().trim());
			} else if (htmlElement.getAttribute("ng-if").equalsIgnoreCase("position.employmentType")) {
				System.out.println("TYPE: "+htmlElement.getTextContent().trim());
			}
		}
		System.out.println("TITLE: "+title.getTextContent().trim());
		System.out.println("DESCRIPTION: "+desc.asText().trim());
	}

}
