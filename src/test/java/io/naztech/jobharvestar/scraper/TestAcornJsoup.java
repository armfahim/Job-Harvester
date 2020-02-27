package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Houzz jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-12
 */
public class TestAcornJsoup extends TestAbstractScrapper{

	private static String URL = "https://www.oaknorth.com/about-us/meet-our-people/";
	private static Document document;
//	private static String HEAD = "https://www.houzz.com";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(URL).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}
	
	@Test
	public void getJobList() {
		Elements jobEl= document.select("a.btn");
		System.out.println(jobEl.size());
	}
	
	@Test
	public void getSummarypage() {
		Elements jobEl= document.select("a.btn");
		for(int i=0;i<jobEl.size();i++) {
			System.out.println(jobEl.get(i).attr("href"));
		}
	}
	
	@Test
	public void getJobDetails() throws IOException {
		Elements jobEl= document.select("a.btn");
		List<String> jobUrl = new ArrayList<>();
		for(int i=0;i<jobEl.size();i++) {
			jobUrl.add(jobEl.get(i).attr("href"));
		}
		for (String string : jobUrl) {
			Document doc = Jsoup.connect(string).get();
			System.out.println(doc.select("div.posting-headline > h2").text()); //title
			System.out.println(doc.select("div.posting-headline > div > div").get(0).text()); //location
			System.out.println(doc.select("div.posting-headline > div > div").get(1).text()); //catagory
			System.out.println(doc.select("div.posting-headline > div > div").get(2).text()); //type
			System.out.println(doc.select("div.postings-btn-wrapper > a").attr("href")); //application url
			System.out.println(doc.select("div.section-wrapper").get(2).text());//spec
		}
	}

}
