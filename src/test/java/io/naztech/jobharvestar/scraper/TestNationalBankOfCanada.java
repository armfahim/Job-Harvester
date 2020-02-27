package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

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
 * @name National Bank of Canada job site Scrapper Test
 * @author Mahmud Rana
 * @since 2019-02-12
 * @url https://jobs.nbc.ca/search-jobs
 */
public class TestNationalBankOfCanada {
	private static final String JOB_SITE_URL = "https://jobs.nbc.ca/search-jobs";
	private static final String PAGE_COUNT_EL_PATH ="//span[@class='pagination-total-pages']";
	private static final String NEXT_BTN_EL_PATH ="//a[@class='next']";
	private static final String JOB_ROW_PATH = "//section[@id='search-results-list']/ul/li/a";
	private static final String JOB_TITLE_EL_PATH = JOB_ROW_PATH+"/h2";
	private static final String DETAIL_PAGE_URL = "https://jobs.nbc.ca/job/montreal/senior-advisor-strategic-alliances-operations/5889/10851122";
	private static final String DETAIL_EL_PATH = "//div[@class='ats-description']";
	private static final String LOC_EL_PATH = DETAIL_EL_PATH+"/h3/span";
	private static final String TYPE_EL_PATH = DETAIL_EL_PATH+"/div[3]/h3/span";
	private static final String CATEGORY_EL_PATH = DETAIL_EL_PATH+"/div[7]/h4";
	private static WebClient client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
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
	public void testPageCount() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(JOB_SITE_URL);
		client.waitForBackgroundJavaScript(12*1000);
		HtmlElement el = page.getBody().getFirstByXPath(PAGE_COUNT_EL_PATH);
		assertEquals(7, Integer.parseInt(el.getTextContent().split(" ")[1].trim()));
	}
	
	@Test
	public void testNextBtnClick() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(JOB_SITE_URL);
		client.waitForBackgroundJavaScript(12*1000);
		List<HtmlElement> rowList = page.getBody().getByXPath(JOB_TITLE_EL_PATH);
		String prevTitle = rowList.get(0).getTextContent();
		System.out.println(prevTitle);
		HtmlElement el = page.getBody().getFirstByXPath(NEXT_BTN_EL_PATH);
		Thread.sleep(2000);
		page = el.click();
		client.waitForBackgroundJavaScript(12*1000);
		rowList = page.getBody().getByXPath(JOB_TITLE_EL_PATH);
		String nxtTitle = rowList.get(0).getTextContent();
		System.out.println(nxtTitle);
		assertTrue(prevTitle.equals(nxtTitle));
	}
	
	@Test
	public void testTitleElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage dPage = client.getPage(DETAIL_PAGE_URL);
		client.waitForBackgroundJavaScript(10*1000);
		HtmlElement el = dPage.getBody().getOneHtmlElementByAttribute("h1", "itemprop", "title");
		assertEquals("Senior Advisor Strategic Alliances - Operations", el.getTextContent());
	}
	
	@Test
	public void testLocationElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage dPage = client.getPage(DETAIL_PAGE_URL);
		client.waitForBackgroundJavaScript(10*1000);
		HtmlElement el = dPage.getBody().getFirstByXPath(LOC_EL_PATH);
		assertEquals("Montreal, Quebec", el.getTextContent());
	}
	
	@Test
	public void testTypeElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage dPage = client.getPage(DETAIL_PAGE_URL);
		client.waitForBackgroundJavaScript(10*1000);
		HtmlElement el = dPage.getBody().getFirstByXPath(TYPE_EL_PATH);
		assertEquals("Full-time", el.getTextContent());
	}
	
	@Test
	public void testCategoryElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage dPage = client.getPage(DETAIL_PAGE_URL);
		client.waitForBackgroundJavaScript(10*1000);
		HtmlElement el = dPage.getBody().getFirstByXPath(CATEGORY_EL_PATH);
		assertEquals("Operations", el.getTextContent().split(":")[1].trim());
	}

}
