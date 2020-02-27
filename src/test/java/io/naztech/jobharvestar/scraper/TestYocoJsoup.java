package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Yoco job site scraper. <br>
 * URL: https://www.yoco.co.za/za/careers/
 * 
 * @author muhammad.tarek
 * @since 2019-04-02
 */
public class TestYocoJsoup {
	private static String url = "https://www.yoco.co.za/za/careers/";
	private static String DETAILPAGEURL = "https://www.yoco.co.za/za/careers/1589817/";
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
		// System.out.println(document.html());
		Elements test = document.select("div.col-md-12 text-center");
		System.out.println(test.text());
	}

	@Test
	public void testJobRow() throws InterruptedException {
		Thread.sleep(10 * 1000);
		Elements jobRowEl = document.getElementsByClass("col-sm-10 text-left");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements spec = document.select("div.col-md-7 jobListing");
		System.out.println(spec.text());
	}

}
