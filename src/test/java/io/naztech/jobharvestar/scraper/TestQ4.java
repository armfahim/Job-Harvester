package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 * Q4 job site parser Test. <br>
 * https://careers.q4inc.com/Join-Q4orce/
 * 
 * @author jannatul.maowa
 * @since 2019-03-27
 */
public class TestQ4 extends TestAbstractScrapper {
	private static WebClient client;
	@Before
	public void setUp() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void testJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String url = "https://careers.q4inc.com/Join-Q4orce/";
		HtmlPage page = client.getPage(url);
		client.waitForBackgroundJavaScript(10 * 1000);
		List<HtmlElement> jobList = page.getByXPath("//span[@class='jobApply']/a");
		System.out.println(jobList.size());
		for (int i = 1; i < jobList.size();) {
			System.out.println(jobList.get(i).getAttribute("href"));
			i = i + 2;
		}
	}

	@Test
	public void testJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String url = "https://q4inc.applytojob.com/apply/4f6V5jgejv/Senior-Surveillance-Analyst";
		Document doc = Jsoup.connect(url).get();
		Element jobE = doc.selectFirst("h1");
		System.out.println(jobE.text());
		jobE = doc.selectFirst("li[title=location]");
		System.out.println(jobE.text());
		jobE = doc.selectFirst("li[id=resumator-job-employment]");
		System.out.println(jobE.text());
		jobE = doc.selectFirst("li[title=Department]");
		System.out.println(jobE.text());
		jobE = doc.selectFirst("div[class=description]");
		System.out.println(jobE.text());
	}
}
