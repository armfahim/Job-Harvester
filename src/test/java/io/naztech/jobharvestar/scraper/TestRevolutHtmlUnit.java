package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Revolut job site parser Test using HtmlUnit.
 * Url: https://www.revolut.com/careers/all
 * 
 * @author jannatul.maowa
 * @since 2019-04-28
 */
public class TestRevolutHtmlUnit extends TestAbstractScrapper {
	private static WebClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void testGetScrapedJobs() throws IOException{
		
		HtmlPage page= client.getPage("https://www.revolut.com/careers/all");
		client.waitForBackgroundJavaScript(TIME_10S * 2);
		List<HtmlElement> jobList=page.getBody().getByXPath("//div[@class='rvl-OpenPositions-fullTitle']");
		System.out.println(jobList.size());
	
	}

}
