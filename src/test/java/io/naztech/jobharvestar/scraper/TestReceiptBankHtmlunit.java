package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @name ReceiptBank jobsite
 * @author Muhammad Bin Farook
 * @since 2019-03-24
 */
public class TestReceiptBankHtmlunit extends TestAbstractScrapper {
	private static final String JOBSITE_URL = "https://www.receipt-bank.com/careers/jobs/?location=All&department=All&role=";
	private static final String BASE_URL = "https://www.receipt-bank.com/";
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
	public void testJobList()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		HtmlPage page = client.getPage(JOBSITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);

		List<HtmlElement> linkAnchor = page.getByXPath("//div[@class='job-table']//a");

		while (page.getByXPath("//div[@class='page-link-next']/*").size() > 0) {

			List<HtmlElement> next = page.getByXPath("//div[@class='page-link-next']/*");
			page = next.get(0).click();
			List<HtmlElement> link1 = page.getByXPath("//div[@class='job-table']//a");
			for (HtmlElement el : link1) {
				linkAnchor.add(el);
			}

		}

		for (HtmlElement el : linkAnchor) {

			HtmlElement loc = el.getFirstByXPath("//span[@class='location']");

			testJobDetails(el.getAttribute("href").toString(), loc.asText());
		}

	}

	private void testJobDetails(String url, String location)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(BASE_URL + url);
		List<HtmlElement> title =  page.getByXPath("//div[@id='job-title']/div[@class='widget-contents']/*");
		System.out.println("JOB TITLE: " + title.get(0).asText());

		;
		System.out.println("JOB Location: " + location);
		List<HtmlElement> discription = page.getByXPath("//div[@id='job-description']/div[@class='widget-contents']/*");

		System.out.println("JOB DESCRIPTION: ");
		System.out.println("---------------");
		System.out.println("---------------");
		String spec = "";
		String requirement = "";
		for (int j = 0; j < discription.size() - 2; j++) {
			if (discription.get(j).asText().contains("Requirements")) {

				while (j < discription.size() - 2) {
					requirement += discription.get(j).asText();
					j++;
				}
				break;
			} else {
				spec += discription.get(j).asText();
			}

		}

		System.out.println("JOB SPEC: " + spec);
		System.out.println();
		System.out.println("JOB REQUIREMENT: " + requirement);
		System.out
				.println("APPLICATION URL: " + discription.get(discription.size() - 1).getAttribute("href").toString());

	}
}
