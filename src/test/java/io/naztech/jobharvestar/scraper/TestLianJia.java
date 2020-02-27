package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLianJia {
	
	private static final String SITE="http://join.lianjia.com/search?k=";
	private static Document document;
	private static String baseUrl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document=Jsoup.connect(SITE).get();
		baseUrl=SITE.substring(0,23);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}

	@Test
	public void testGetJobList() {
		Elements jobList= document.getElementsByTag("tbody").select(" > *");
		for (int i = 1; i < jobList.size(); i++) {
			System.out.println("URL:->"+baseUrl+jobList.get(i).child(0).child(0).attr("href"));
			System.out.println("TITLE:->"+jobList.get(i).child(0).child(0).text().trim());
			System.out.println("CATEGORY:->"+jobList.get(i).child(1).text().trim());
			System.out.println("LOCATION:->"+jobList.get(i).child(2).text().trim());
			System.out.println("DATE:->"+jobList.get(i).child(3).text().trim());
		}
	}
	
	@Test
	public void testGetJobDetails() throws IOException {
		String link="http://join.lianjia.com/zpdetail/270131807?k=";
		document=Jsoup.connect(link).get();
		Element details= document.selectFirst(".xiangqingtext");
		Element apply= document.selectFirst("#apply");
		System.out.println("DESC:->"+details.text().trim());
		System.out.println("APPLY URL:->"+baseUrl+apply.attr("url"));
	}

}
