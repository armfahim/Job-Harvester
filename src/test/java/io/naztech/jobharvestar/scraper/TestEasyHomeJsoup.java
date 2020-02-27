package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Easy Home job site scraper. <br>
 * URL: https://goeasy.talentnest.com/en
 *
 * @author a.s.m. tarek
 * @since 2019-03-12
 */

public class TestEasyHomeJsoup {
	private static String url = "https://goeasy.talentnest.com/en";
	private static String DETAILPAGEURL = "https://goeasy.talentnest.com/en/posting/44618";
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
	}

	@Test
	public void testJobRow() {
		Elements jobRowEl = document.select("div.rounded-corners posting");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements title = document.select("div.posting-header").select("div.field");
		System.out.println(title.text());
	}

}

