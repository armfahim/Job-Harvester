package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPrudential {

	private static WebClient client;
	private static final String SITE_URL = "http://jobs.prudential.com/job-listing.php";
	private static final String BASE_URL = "http://jobs.prudential.com/";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(30*1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30*1000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

	@Test
	public void TestTotalJob() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			List<HtmlElement> jobList = page.getByXPath("//section[@id='recentlyPosted']/div/div/div");
			assertEquals(4,jobList.size());
			//System.out.println(jobList.size());
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info("Connection Failed");
		}
	}
	
	@Test
	public void TestFirstJobUrl() {
		try {
			HtmlPage page = client.getPage(SITE_URL);
			List<HtmlElement> jobList = page.getByXPath("//section[@id='recentlyPosted']/div/div/div");
			String job = jobList.get(0).getElementsByTagName("a").get(0).getAttribute("href");
			page = client.getPage(BASE_URL+job);
			List<HtmlElement> jobDetail = page.getByXPath("//div[@id='job-details']/div");
			log.info(jobDetail.get(0).getElementsByTagName("div").get(0).getTextContent());
			
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info("Connection Failed");
		}
	}

}
