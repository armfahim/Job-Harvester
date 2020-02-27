package io.naztech.jobharvestar.scraper;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Test Ellington Group jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-07
 */
public class TestEllingtonGroup extends TestAbstractScrapper{

	private static final String SITE = "https://www.ellington.com/careers/job-search/";
	private static WebClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		HtmlElement button = page.getFirstByXPath("//button[@class = 'btn btn-primary oracletaleocwsv2-btn-fa fa-search']");
		page = button.click();
		Thread.sleep(TIME_5S);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='oracletaleocwsv2-accordion oracletaleocwsv2-accordion-expandable clearfix']/div");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testGetJobDetails() throws IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		HtmlElement button = page.getFirstByXPath("//button[@class = 'btn btn-primary oracletaleocwsv2-btn-fa fa-search']");
		page = button.click();
		Thread.sleep(TIME_5S);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='oracletaleocwsv2-accordion oracletaleocwsv2-accordion-expandable clearfix']/div");
		List<String> job = new ArrayList<>();
		for(HtmlElement el : jobLinksE) {
			System.out.println("url: "+ el.getElementsByTagName("a").get(0).getAttribute("href"));
			job.add(el.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println("title: "+ el.getElementsByTagName("a").get(0).asText());
		}
		for(String str : job) {
			page = client.getPage(str);
			client.waitForBackgroundJavaScript(8000);
			HtmlElement spec = (HtmlElement) page.getByXPath("//div[@class='col-xs-12 col-sm-12 col-md-8']").get(0);
			System.out.println(spec.asText());
			
		}
	}

}
