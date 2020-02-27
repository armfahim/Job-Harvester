package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 * @name Aflac jobsite pareser
 * @author Mahmud Rana
 * @since 2019-02-11
 */
public class TestAflac {
	private static final String JOBSITE_URL = "https://aflac.taleo.net/careersection/external/jobsearch.ftl";
	private static final String FIRST_JOB_LINK_ID = "requisitionListInterface.reqTitleLinkAction.row1";
	private static final String JOB_TITLE_EL_ID = "requisitionDescriptionInterface.reqTitleLinkAction.row1";
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
	public void testJobDetailElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(JOBSITE_URL);
		client.waitForBackgroundJavaScript(20*1000);
		HtmlElement el = (HtmlElement) page.getElementById(FIRST_JOB_LINK_ID);
		Thread.sleep(500);
		page = el.click();
		client.waitForBackgroundJavaScript(20*1000);
		el = (HtmlElement) page.getElementById(JOB_TITLE_EL_ID);
		assertEquals("Group Representative, Portland, OR", el.getTextContent());
	}
	


}
