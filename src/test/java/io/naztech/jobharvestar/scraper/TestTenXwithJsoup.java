package io.naztech.jobharvestar.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
public class TestTenXwithJsoup {
	private static String url = "https://careers.ten-x.com/";
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
		Elements jobEl= document.getElementsByClass("au-target");
		System.out.println(jobEl.size());
	}


}
