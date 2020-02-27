package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Test UNIHTC jobs site parsing using htmlunit.
 * @author Kayumuzzaman Robin
 * @since 2019-03-18
 */

public class TestUnitedImagingHealthcareHtmlunit extends TestAbstractScrapper {
	private static final String SITE = "https://usa.united-imaging.com/careers/";
	private static WebClient client = null;
	final WebWindow topLevelWindow = client.getTopLevelWindows().get(0);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		topLevelWindow.setInnerWidth(300);
		topLevelWindow.setInnerHeight(600);
		HtmlPage page = client.getPage(SITE);
		List<Object> jobSize = page.getByXPath("//div[@class='l-career__item _flex']");
		System.out.println("Total Job: " + jobSize.size());
	}

	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		topLevelWindow.setInnerWidth(300);
		topLevelWindow.setInnerHeight(600);

		HtmlPage page = client.getPage(SITE);
		List<HtmlElement> jobLinks = page.getByXPath("//div[@class='l-career__item _flex']/div/a");
		for (HtmlElement htmlElement : jobLinks) {
			System.out.println(htmlElement.getAttribute("href"));
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		topLevelWindow.setInnerWidth(300);
		topLevelWindow.setInnerHeight(600);

		HtmlPage page = client.getPage(SITE);
		List<HtmlElement> jobLinks = page.getByXPath("//div[@class='l-career__item _flex']/div/a");
		List<String> links = new ArrayList<String>();
		
		for (HtmlElement el : jobLinks) {
			links.add(el.getAttribute("href"));
		}

		for (String string : links) {
			HtmlPage page2 = client.getPage(string);
			HtmlElement title = (HtmlElement) page2.getByXPath("//div[@class='container']/h1").get(0);
			System.out.println("Job Title: " + title.asText());
			HtmlElement location = (HtmlElement) page2.getByXPath("//div[@class='container']/h3").get(0);
			System.out.println("Job Location: " + location.asText());
			HtmlElement applicationUrl = (HtmlElement) page2.getByXPath("//div[@class='s-position__buttons']/a").get(0);
			System.out.println("Job Application Link: " + applicationUrl.getAttribute("href"));
			HtmlElement spec = (HtmlElement) page2.getFirstByXPath("//div[@class='_wysiwyg']");
			System.out.println("Job Specs: " + spec.asText());
		}
	}
}
