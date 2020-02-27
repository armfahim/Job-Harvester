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
 * @name Bank of QueensLAnd Parser Test
 * @author Mahmud Rana
 * @since 2019-02-11
 * @url https://boq-openhire.silkroad.com/epostings/index.cfm?fuseaction=app.allpositions&company_id=16496&version=1
 */
public class TestBankOfQueensLand {
	private static final String JOB_SITE_URL = "https://boq-openhire.silkroad.com/epostings/index.cfm?fuseaction=app.allpositions&company_id=16496&version=1";
	private static final String JOB_DETAIL_URL = "https://boq-openhire.silkroad.com/epostings/index.cfm?fuseaction=app.jobinfo&jobid=6813&company_id=16496&version=1&source=ONLINE&jobOwner=993152&aid=1";
	private static final String JOB_TITLE_EL_ID = "jobTitleDiv";
	private static final String JOB_ROW_EL_PATH = "//a[@class='cssAllJobListPositionHref']";
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
	public void testDetailPageLoad() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(JOB_DETAIL_URL);
		client.waitForBackgroundJavaScript(12*1000);
		HtmlElement el = (HtmlElement) page.getElementById(JOB_TITLE_EL_ID);
		assertEquals("Executive Assistant", el.getTextContent().trim());
	}
	
	@Test
	public void testSummaryPageLoad() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(JOB_SITE_URL);
		client.waitForBackgroundJavaScript(12*1000);
		List<HtmlElement> rowList = page.getBody().getByXPath(JOB_ROW_EL_PATH);
		assertEquals(62, rowList.size());
	}
	

}
