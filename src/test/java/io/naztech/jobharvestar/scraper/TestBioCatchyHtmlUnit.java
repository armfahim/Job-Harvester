package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * BioCatchy job site parser Test in HtmlUnit.
 * https://www.biocatch.com/biometrics-cybersecurity-careers
 * 
 * @author jannatul.maowa
 * @since 2019-03-25
 */
public class TestBioCatchyHtmlUnit extends TestAbstractScrapper {
	private static WebClient client;

	@Before
	public void setUp() throws Exception {
		client = getFirefoxClient();
	}

	// Selenium use kore korsi
	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String url = "https://www.biocatch.com/biometrics-cybersecurity-careers";
		HtmlPage page = client.getPage(url);
		client.waitForBackgroundJavaScript(40 * 1000);
		List<HtmlElement> jobList = page.getByXPath("//a[@class='accordion-section-title job-title']");
		System.out.println(jobList.size());

	}

	@Test
	public void jobDetailsTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String url = "https://www.comeet.co/jobs/biocatch/03.00E/threat-analyst---latam-and-iberia/DD.B0B";

		HtmlPage page = client.getPage(url);
		HtmlElement body = page.getBody();
		System.out.println(body.getOneHtmlElementByAttribute("h2", "class", "positionName").getTextContent().trim());
		System.out.println(
				body.getOneHtmlElementByAttribute("li", "ng-if", "position.department").getTextContent().trim());
		System.out
				.println(body.getOneHtmlElementByAttribute("li", "ng-if", "position.location").getTextContent().trim());
		System.out.println(
				body.getOneHtmlElementByAttribute("li", "ng-if", "position.employmentType").getTextContent().trim());
		HtmlElement body1 = page.getFirstByXPath("//div[@class='userDesignedContent company-description']");
		System.out.println(body1.getTextContent());
	}
}
