package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestSofiHtmlUnit {

	private static Logger log = LoggerFactory.getLogger(TestSofiHtmlUnit.class);
	private static final String SAMPLE_DETAIL_PAGE_URL = "https://jobs.jobvite.com/careers/sofi/job/oWQA9fwf?__jvst=Career+Site";
	private static final String SAMPLE_SUMMARY_PAGE_URL = "https://www.sofi.com/careers/";
	private static WebClient webClient;
	private static HtmlPage page;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
		webClient.waitForBackgroundJavaScript(20 * 1000);
		webClient.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
		webClient.getOptions().setTimeout(60 * 1000);
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void testGetJobList() {
		try {
			page = webClient.getPage(SAMPLE_SUMMARY_PAGE_URL);
			webClient.waitForBackgroundJavaScript(5 * 1000);
			List<HtmlAnchor> rowiElements = page.getByXPath("//div[@class='listings']/div/a");
			for (HtmlAnchor htmlAnchor : rowiElements) {
				System.out.println("Job URL: " + htmlAnchor.getAttribute("data-link"));
				System.out.println(htmlAnchor.getElementsByTagName("span").get(0).getTextContent().trim());
				System.out.println(htmlAnchor.getElementsByTagName("span").get(1).getTextContent().trim());
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Error on testing Summary Page", e, e);
		}
	}

	@Test
	public void testJobDetailPage() {
		try {
			page = webClient.getPage(SAMPLE_DETAIL_PAGE_URL);
			List<DomElement> detailsPage = page.getByXPath("//div[@class='jv-wrapper']");
			List<HtmlAnchor> jobApplyUrl = page.getByXPath("//a[@class='jv-button jv-button-primary jv-button-apply']");
			System.out.println("Job Url: "+"https://www.sofi.com/careers"+jobApplyUrl.get(0).getAttribute("href"));
			System.out.println("Job Title: " + detailsPage.get(1).getElementsByTagName("h2").get(0).getTextContent().trim());
			String category = detailsPage.get(1).getElementsByTagName("p").get(0).getTextContent().trim();
			String[] part = category.split("\n");
			String location = "";
			System.out.println("Job Category: " + part[0].trim());
			for (int j = 1; j < part.length; j++)
				location += part[j].trim() + " ";
			System.out.println("Job Location: " + location.trim());
			HtmlElement desEl = page.getBody().getOneHtmlElementByAttribute("div", "class","jv-job-detail-description");
			System.out.println("Description: " + desEl.getTextContent().trim());
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Error on testing Detail Page", e, e);
		}
	}

}
