package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 * Lincoln National Corp job site parser test.
 * https://jobs.lincolnfinancial.com/search
 * 
 * @author Nuzhat Tabassum
 * @since 2019-02-12
 */

public class TestLincolnNationalCorp {
	private static WebClient client;
	private static final String SITE_URL = "https://jobs.lincolnfinancial.com/search";
	private static final String FIRST_JOB_EL_ID = "//div[@class='searchResultsShell']/table/tbody/tr/td";
	private static final String JOB_PER_PAGE_EL_ID = "//div[@class='searchResultsShell']/table/tbody/tr";
	private static final String TOTAL_JOB_EL_ID = "//span[@class='paginationLabel']/b";
	private static final String JOB_ROW_EL_ID = "//a[@class='jobTitle-link']";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.CHROME);
		client.getOptions().setTimeout(30 * 1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30 * 1000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

	@Test
	public void testUrl() {
		String url = "https://jobs.lincolnfinancial.com/search";
		System.out.println(url.substring(0, 33));
	}

	@Test
	public void testDateParse() {
		LocalDate dt = LocalDate.parse("Feb 11, 2019", DateTimeFormatter.ofPattern("MMM d, yyyy"));
		System.out.println(dt);
		LocalDate dt2 = LocalDate.parse("Feb 9, 2019", DateTimeFormatter.ofPattern("MMM d, yyyy"));
		System.out.println(dt2);
	}

	@Test
	public void TestFirstJobUrl() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			List<HtmlElement> jobUrl = page.getByXPath(FIRST_JOB_EL_ID);
			System.out.println(
					"https://jobs.lincolnfinancial.com/search" + jobUrl.get(0).getElementsByTagName("a").get(0).getAttribute("href"));
		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println("Connection Failed");
		}
	}

	@Test
	public void TestJobPerPage() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			List<HtmlElement> jobList = page.getByXPath(JOB_PER_PAGE_EL_ID);
			System.out.println(jobList.size());
		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println("Connection Failed");
		}
	}

	@Test
	public void testTotalJob() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			List<HtmlElement> pageNo = page.getByXPath(TOTAL_JOB_EL_ID);
			int a = Integer.parseInt(pageNo.get(1).asText());
			System.out.println(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSummaryPageLoad() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(12 * 1000);
		List<HtmlElement> rowList = page.getBody().getByXPath(JOB_ROW_EL_ID);
		System.out.println(rowList.size());
	}

}
