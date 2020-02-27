package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInlineFrame;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 * DJI_innovation job parsing class<br>
 * URL: https://we.dji.com/jobs_en.html
 * 
 * @author Muhammad Bin Farook
 * @since: 2019-03-19
 */
public class TestDjiHtmlunit {

	private static final String JOBSITE_URL = "https://we.dji.com/jobs_en.html";
	private static final String BASE_URL = "https://we.dji.com/";
	private static WebClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(30 * 1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);

		client.setJavaScriptTimeout(30 * 1000);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		HtmlPage page = client.getPage(JOBSITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);

		List<HtmlElement> div = page.getByXPath("//div[@class='col-md-10 col-md-offset-1']//a");

		List<String> joblink = new ArrayList<String>();
		for (HtmlElement el : div)
			if (el.getAttribute("href").contains("detail_en.html#"))
				joblink.add(el.getAttribute("href").toString());

		HtmlElement next = page.getFirstByXPath("//a[@aria-label='Next']");

		page = next.click();

		String str = "";
		while (true) {

			List<HtmlElement> divs = page.getByXPath("//div[@class='col-md-10 col-md-offset-1']//a");

			for (HtmlElement el : divs)
				if (el.getAttribute("href").contains("detail_en.html#"))
					joblink.add(el.getAttribute("href").toString());

			next = page.getFirstByXPath("//a[@aria-label='Next']");

			page = next.click();
			List<HtmlElement> el = page.getByXPath("//ul[@id='MessPage']/*");
			str = el.get(7).getAttribute("class").toString();
			if (str.contains("disabled"))
				break;

		}

		List<HtmlElement> divs = page.getByXPath("//div[@class='col-md-10 col-md-offset-1']//a");

		for (HtmlElement el : divs)
			if (el.getAttribute("href").contains("detail_en.html#"))
				joblink.add(el.getAttribute("href").toString());

		testSummeryPage(joblink);

	}

	private void testSummeryPage(List<String> joblink)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		for (int i = 0; i < joblink.size(); i++) {
			client = new WebClient(BrowserVersion.FIREFOX_52);
			client.getOptions().setTimeout(30 * 1000);
			client.getOptions().setUseInsecureSSL(true);
			client.getCookieManager().setCookiesEnabled(true);
			client.getOptions().setThrowExceptionOnScriptError(false);
			client.getOptions().setThrowExceptionOnFailingStatusCode(false);

			System.out.println(BASE_URL + joblink.get(i));
			HtmlPage page = client.getPage(BASE_URL + joblink.get(i));

			System.out.println("JOB TITLE: " + page.getElementsByTagName("h1").get(0).asText());
			
			System.out.println("JOB APPLICATION URL: "+BASE_URL+page.getAnchorByText("Apply Now").getAttribute("href"));

			List<HtmlElement> el = page.getByXPath("//div[@class='col-xs-12 col-sm-4 mb20 col-sm-offset-2']/*");

			String str1 = el.get(1).asText();

			String[] str2 = str1.split("\n");
			

			System.out.println("JOB Location: " + str2[1]);

			List<HtmlElement> ell = page.getByXPath("//div[@class='col-xs-12 col-sm-4 mb20']/*");
			String str3 = ell.get(1).asText();
			String[] str4 = str3.split("\n");
			System.out.println("JOB CATEGORY: " + str4[1]);

			List<HtmlElement> des = page.getByXPath("//div[@class='well']/article");
			String spec = "";
			String req = "";

			for (HtmlElement li : des) {
				if (li.getElementsByTagName("h4").get(0).getTextContent().contains("Responsibilities: "))
					spec += li.asText().toString();

				else if (li.getElementsByTagName("h4").get(0).getTextContent().contains("Description: "))
					spec += li.asText().toString();
				else if (li.getElementsByTagName("h4").get(0).getTextContent().contains("Requirements: "))
					req += li.asText().toString() + "\n";
				else if (li.getElementsByTagName("h4").get(0).getTextContent().contains("Preferred: "))
					req += li.asText().toString();
			}

			System.out.println("JOB SPEC: \n" + spec);
			System.out.println("JOB REQ: \n" + req);

			System.out.println();

		}

	}

}