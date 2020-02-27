package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

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
 * Test Grail jobs site parsing using jsoup.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-12
 */
public class TestGrail extends TestAbstractScrapper{

	private static String URL = "https://grail.com/careers/career-listings/";
	private static Document document;
	private static String HEAD = "https://grail.com";
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
		Elements jobEl= document.select("section.row-listing > div");
		System.out.println(jobEl.size());
	}
	
	@Test
	public void getFirstPage() {
		Elements jobEl= document.select("section.row-listing > div");
		for(int i=0;i<jobEl.size();i++) {
			System.out.println(jobEl.get(i).select("a").get(0).attr("href"));
			System.out.println(jobEl.get(i).select("a").get(1).attr("href"));
			System.out.println(jobEl.get(i).select("h2").text());
			System.out.println(jobEl.get(i).select("span").get(0).text());
			System.out.println(jobEl.get(i).select("span").get(1).text());
		}
	}
	
	@Test
	public void getJobDetailsPage() throws IOException {
		Elements jobEl= document.select("section.row-listing > div");
		List<String> url = new ArrayList<>();
		for(int i=0;i<jobEl.size();i++) {
			System.out.println(jobEl.get(i).select("a").get(0).attr("href"));
			url.add(jobEl.get(i).select("a").get(1).attr("href"));
		}
		
		for (String string : url) {
			Document documentNew = Jsoup.connect(HEAD+string).get();
			System.out.println(documentNew.select("main.main > section").get(2).select("div").text());
		}
	}
	
	

}
