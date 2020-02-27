package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Zomato Media job site scraper. <br>
 * URL: https://zomato.recruitee.com/
 * 
 * @author a.s.m. tarek
 * @since 2019-03-14
 */
public class TestZomatoMediaJsoup {
	private static String url = "https://zomato.recruitee.com/";
	private static String DETAILPAGEURL = "https://zomato.recruitee.com/o/assistant-manager-tax";
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
	public void testFirtstPageLoaded() throws IOException {
		 System.out.println(document.html());
		Elements test = document.select("h3.section-title");
		System.out.println(test.get(0).text());
	}

	@Test
	public void testJobRow() {
		Elements jobRowEl = document.select("div.job");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements spec = document.select("div.description");
		System.out.println(spec.get(0).text());
		Elements req = document.select("div.description");
		System.out.println(req.get(1).text());
	}

}
