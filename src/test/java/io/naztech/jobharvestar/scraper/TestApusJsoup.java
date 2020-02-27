package io.naztech.jobharvestar.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Apus jobs site parsing using jsoup.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-11
 */
public class TestApusJsoup extends TestAbstractScrapper{

	private static String url = "http://www.apusapps.com/en/jobs/";
	private static Document document;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(url).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}
	
	@Test
	public void getJobList() {
		Elements jobEl= document.select("div.join-us-layer > div > div > ul");
		System.out.println(jobEl.size());
	}
	
	@Test
	public void getJobDetails() {
		Elements jobEl= document.select("div.join-us-layer > div > div > ul");
		for(int i=0;i<jobEl.size();i++) {
			System.out.println(jobEl.get(i).select("h4").text());
			System.out.println(jobEl.get(i).text());
		}
	}

}
