package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestAbstractGHFrames extends TestAbstractScrapper{

	private static final String SITE = "https://iextrading.com/careers/#open-positions";
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
		webClient.waitForBackgroundJavaScript(TIME_10S);
		HtmlPage innerPage=null;
		List<FrameWindow> frames = page.getFrames();
		for (FrameWindow frame : frames) {
		    if (frame.getFrameElement().getId().equals("grnhse_iframe")) {
		        innerPage = (HtmlPage) frame.getEnclosedPage();
		        break;
		    }
		}
		
		List<HtmlElement> categoryElements = innerPage.getByXPath("//div[@class='opening']");
		for (HtmlElement htmlElement : categoryElements) {
			System.out.println("JOB URL: "+htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println("TITLE: "+htmlElement.getElementsByTagName("a").get(0).getTextContent());
			System.out.println("LOCATION: "+htmlElement.getElementsByTagName("span").get(0).getTextContent());
		}
	}
	
	@Test
	public void testGetJobDetails() throws IOException {
		String link = "https://www.allbirds.com/pages/careers?gh_jid=1568065";
		HtmlPage page = webClient.getPage(link);
		webClient.waitForBackgroundJavaScript(5 * 1000);
		HtmlPage innerPage=null;
		List<FrameWindow> frames = page.getFrames();
		for (FrameWindow frame : frames) {
		    if (frame.getFrameElement().getId().equals("grnhse_iframe")) {
		        innerPage = (HtmlPage) frame.getEnclosedPage();
		        break;
		    }
		}
		List<HtmlElement> row = innerPage.getByXPath("//div[@id='content']/*");
		String desc = "";
		if (row.size() > 3) {
			for (int i = 0; i < row.size(); i++) {
				if (i==0) System.out.println("CATEGORY: "+row.get(i).getTextContent().split("\\|")[1].trim());
				else desc+=row.get(i).asText().trim()+"\n";
			}
		}
		System.out.println("DESCRIPTION: "+desc);
	}
}
