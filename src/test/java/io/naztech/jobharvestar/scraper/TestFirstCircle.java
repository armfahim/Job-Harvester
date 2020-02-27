package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestFirstCircle extends TestAbstractScrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String SITE = "https://www.firstcircle.ph/roles";
	private static final String HEAD_APPLY = "https://first-circle.breezy.hr";
	private static WebClient webClient;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient=getChromeClient();
	}

	@AfterClass
	public static void afterClass() {
		webClient.close();
	}

	@Test
	public void testGetJobList() {
		try {
			HtmlPage page= webClient.getPage(SITE);
			webClient.waitForBackgroundJavaScript(TIME_5S);
			List<HtmlElement> jobUrlList = page.getByXPath("//li[@class='bzOpening']/a");
			List<String> jobUrl = new ArrayList<String>();
			for (HtmlElement url : jobUrlList) {
				System.out.println("URL: "+url.getAttribute("onClick").split("\\?")[0].replace("bzPopupCenter('", "").trim());
				jobUrl.add(url.getAttribute("onClick").split("\\?")[0].replace("bzPopupCenter('", "").trim());
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Page not able to connect"+e);
		}
	}
	
	@Test
	public void testGetJobDetails() {
		String link = "https://first-circle.breezy.hr/p/f772e7153aa8-digital-marketing-specialist";
		try {
			HtmlPage page = webClient.getPage(link);
			HtmlElement banner = page.getBody().getOneHtmlElementByAttribute("div", "class", "banner");
			HtmlElement desc = page.getBody().getOneHtmlElementByAttribute("div", "id", "description");
			System.out.println("TITLE: "+banner.getElementsByTagName("h1").get(0).getTextContent().trim());
			System.out.println("LOCATION: "+banner.getElementsByAttribute("li", "class", "location").get(0).getTextContent().trim());
			System.out.println("TYPE: "+banner.getElementsByAttribute("span", "class", "polygot").get(0).getTextContent().trim());
			System.out.println("CATEGORY: "+banner.getElementsByAttribute("li", "class", "department").get(0).getTextContent().trim());
			System.out.println("APPLY URL: "+HEAD_APPLY+desc.getElementsByAttribute("a", "class", "polygot-parent button green large bzyButtonColor polygot").get(0).getAttribute("href"));
			System.out.println("DESCRIPTION: "+desc.getTextContent().trim());
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Page not able to connect"+e);
		}
	}
}
