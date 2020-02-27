package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Grab Taxi jobs site parsing using jsoup.
 *  
 * @author Armaan Choudhury
 * @since 2019-03-12
 */
public class TestGrabTaxi extends TestAbstractScrapper{

	private static String url = "https://grab.careers/jobs/";
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
		Elements jobEl= document.select("div.dataTables_wrapper no-footer > table > tbody > tr");
		System.out.println(jobEl.size());
	}
	

}
