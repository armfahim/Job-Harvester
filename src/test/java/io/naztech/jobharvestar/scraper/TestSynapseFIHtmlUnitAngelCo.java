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
 * SynapseFI job site parse Test in Htmlunit <br>
 * https://angel.co/synapsefi/jobs
 * 
 * @author jannatul.maowa
 * @since 2019-03-28
 */
public class TestSynapseFIHtmlUnitAngelCo extends TestAbstractScrapper {
	private static WebClient client;

	@Before
	public void setUp() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void testJobListTitle() throws IOException {

		String url = "https://angel.co/synapsefi/jobs";
		HtmlPage page = client.getPage(url);
		List<HtmlElement> jobList = page.getByXPath("//a[@class='u-fontSize18']");
		System.out.println(jobList.size());
		for (int i = 0; i < jobList.size(); i++) {
			System.out.println(jobList.get(i).getAttribute("href"));
		}
	}

	@Test
	public void testJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String url = "https://angel.co/synapsefi/jobs/324115-ux-designer";
		HtmlPage page = client.getPage(url);
		client.waitForBackgroundJavaScript(40 * 1000);
		HtmlElement body = page.getBody();
		System.out.println(body.getOneHtmlElementByAttribute("h1", "class", "u-colorGray3").getTextContent().trim());
		String string = body
				.getOneHtmlElementByAttribute("div", "class", "high-concept s-vgBottom2 u-colorGray6 u-fontSize16")
				.getTextContent().trim();
		if (string.contains(" · ")) {
			String[] strs = body
					.getOneHtmlElementByAttribute("div", "class", "high-concept s-vgBottom2 u-colorGray6 u-fontSize16")
					.getTextContent().trim().split(" · ", 2);
			System.out.println(strs.length);
			System.out.println(strs[0]);
			System.out.println(strs[1]);
		} else {
			System.out.println(string);
		}
		System.out
				.println(body.getOneHtmlElementByAttribute("div", "class", "job-description u-fontSize14 u-colorGray6")
						.getTextContent().trim());
		List<HtmlElement> preRequisite = page.getByXPath("//div[@class='s-vgBottom2']");
		for (int i = 0; i < preRequisite.size(); i++) {

			if (preRequisite.get(i).getTextContent().contains("$"))
				System.out.println(preRequisite.get(i).getTextContent());

			else {
				System.out.println("skill");
			}
		}
	}
}
