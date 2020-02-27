package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestPalantirTechnologies extends TestAbstractScrapper{

	private static final String SITE = "https://www.palantir.com/careers/";
	private static WebClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<HtmlElement> jobListE = page.getByXPath("//div[@class = 'positions-list']/div/ul/li/ul/li");
		for(HtmlElement el : jobListE) {
			List<HtmlElement> links = el.getElementsByTagName("a");
			System.out.println(links.get(0).getAttribute("href"));
		}
		System.out.println(jobListE.size());
	}
	
	@Test
	public void testGetJobDetails() throws IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<HtmlElement> jobListE = page.getByXPath("//div[@class = 'positions-list']/div/ul/li/ul/li");
		List<String> jobLink = new ArrayList<>();
		for(HtmlElement el : jobListE) {
			List<HtmlElement> links = el.getElementsByTagName("a");
			jobLink.add(links.get(0).getAttribute("href"));
		}
		for (String string : jobLink) {
			page = client.getPage(string);
			client.waitForBackgroundJavaScript(TIME_5S);
			HtmlElement title = page.getFirstByXPath("//div[@class='posting-headline']/h2");
			System.out.println(title.getTextContent());
			HtmlElement location = page.getFirstByXPath("//div[@class='posting-categories']/div[1]");
			System.out.println(location.getTextContent());
			HtmlElement spec = (HtmlElement) page.getByXPath("//div[@class='section page-centered']").get(0);
			System.out.println(spec.getTextContent());
			HtmlElement preReq = (HtmlElement) page.getByXPath("//div[@class='section page-centered']").get(1);
			System.out.println(preReq.getTextContent());
		}
	}

}
