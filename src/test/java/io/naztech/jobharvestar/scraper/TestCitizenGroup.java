package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

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
 * @name Citizens Bank JobSite Scraper Test
 * @author Mahmud Rana
 * @since 2019-02-10
 * @url https://jobs.citizensbank.com/search-jobs
 */
public class TestCitizenGroup {
	private static WebClient client;
	private static final String SITE_URL = "https://jobs.citizensbank.com/search-jobs";
	private static final String DETAIL_PAGE_URL = "https://jobs.citizensbank.com/job/boston/commercial-real-estate-portfolio-manager-i/288/10846332";
	private static final String PAGE_COUNT_EL_PATH = "//span[@class='pagination-total-pages']";
	private static final String JOB_ROW_EL_PATH = "//section[@id='search-results-list']/ul/li";
	private static final String NEXT_PAGE_EL_PATH = "//a[@class='next']";
	private static final String JOB_TITLE_EL_PATH = "//h1[@itemprop='title']";
	private static final String JOB_INFO_EL_PATH = "//section[@class='job-description']";
	private static final String POST_DATE_EL_PATH = JOB_ROW_EL_PATH+"/a/span[@class='job-date-posted']";

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
	public void testTotalPageCount() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);
		HtmlElement el = page.getBody().getFirstByXPath(PAGE_COUNT_EL_PATH);
		assertEquals("55", el.getTextContent().split("of")[1].trim());
	}

	@Test
	public void testJobPerPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);
		List<HtmlElement> rowList = page.getBody().getByXPath(JOB_ROW_EL_PATH);
		assertEquals(15, rowList.size());
	}
	
	@Test
	public void testJobRowChild() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);
		HtmlElement el = page.getBody().getFirstByXPath(JOB_ROW_EL_PATH+"/a/h2");
		assertEquals("Commercial Real Estate Portfolio Manager I", el.getTextContent());
	}
	
	@Test
	public void testNextPageBtn() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);
		HtmlElement el = page.getBody().getFirstByXPath(NEXT_PAGE_EL_PATH);
		page = el.click();
		client.waitForBackgroundJavaScript(20*1000);
		el = page.getBody().getFirstByXPath(JOB_ROW_EL_PATH+"/a/h2");
		assertEquals("Part Time Customer Service Teller",el.getTextContent());
	}
	
	@Test
	public void testDetailPageLoaded() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(DETAIL_PAGE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);
		HtmlElement el = page.getBody().getFirstByXPath(JOB_TITLE_EL_PATH);
		System.out.println();
		assertEquals("Commercial Real Estate Portfolio Manager I", el.getTextContent());
	}
	
	@Test
	public void testSampleElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(DETAIL_PAGE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);
		HtmlElement el = (HtmlElement) page.getBody().getByXPath(JOB_INFO_EL_PATH+"/span").get(2);
		System.out.println(" Text: "+el.getTextContent());
		assertEquals("Lending", el.getTextContent().split("Category")[1].trim());
	}
	
	@Test
	public void testJobPostedDateElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(20 * 1000);
		HtmlElement el = page.getBody().getFirstByXPath(POST_DATE_EL_PATH);
		assertEquals("02/09/2019", el.getTextContent());
	}

}
