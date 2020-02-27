package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 
 * @author tanmoy.tushar
 * @since May 6, 2019
 */
public class TestUberHtmlUnit extends TestAbstractScrapper {
	private static String URL = "https://www.uber.com/us/en/careers/list/";
	private static String ADD_URL = "/global/en";

	private static WebClient client = null;
	private String baseUrl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

//	@Test
	public void testGetList(HtmlPage page)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
//		client = getFirefoxClient();
//		HtmlPage page = client.getPage(url);
//		client.waitForBackgroundJavaScript(TIME_10S * 4);
		List<HtmlElement> jobList = page.getByXPath("//span[@class='ot']/a");
		System.out.println(jobList.size());
		for(int i = 0; i < jobList.size(); i++) {
			System.out.println(jobList.get(i).asText());
			System.out.println(getBaseUrl() + ADD_URL + jobList.get(i).getAttribute("href"));
		}		
	}
	
	@Test
	public void testGetShowMoreButton() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		String url = "https://www.uber.com/us/en/careers/list/";
		client = getFirefoxClient();
		HtmlPage page = client.getPage(url);
		testGetList(page);
		DomElement showMoreBtn = page.getFirstByXPath("//button[@class='ft nu qa lv qb be gw qc h2 ag fz qd']");
		System.out.println(showMoreBtn.asText());
		page = showMoreBtn.click();
		client.waitForBackgroundJavaScript(TIME_10S);
		Thread.sleep(TIME_10S);
		testGetList(page);
//		for(int i = 0; i < 5; i++) {
//			showMoreBtn.click();
//			client.waitForBackgroundJavaScript(TIME_10S);
//			Thread.sleep(TIME_10S);
//			testGetList(page);
//		}		
	}
	
	@Test
	public void testDetailsPage() throws IOException {
		String url = "https://www.uber.com/global/en/careers/list/50624/";
		Document doc = Jsoup.connect(url).get();
		Element jobE = doc.selectFirst("h1");
		System.out.println(jobE.text());
		jobE = doc.selectFirst("div[class=o5 be nn gw o9]");
		String[] parts = jobE.text().split(" in ");
		System.out.println(parts[0].trim());
		System.out.println(parts[1].trim());
		jobE = doc.selectFirst("div[class=bq bz c0]>a");
		System.out.println(getBaseUrl() + jobE.attr("href"));
		jobE = doc.selectFirst("div[class=o5 nn]");
		System.out.println(jobE.text());		
	}
	
	public String getBaseUrl() {
		return URL.substring(0, 20);
	}
}