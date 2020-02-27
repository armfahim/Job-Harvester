package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * TRUVAL job site scraper. <br>
 * URL: https://truvalue-labs.breezy.hr/
 * 
 * @author Md. Sanowar Ali
 * @since 2019-05-07
 */
public class TestTruvalueLabs {
	private static String SITE_URL = "https://truvalue-labs.breezy.hr/";
	private static String DETAILPAGEURL = "https://truvalue-labs.breezy.hr/p/c3d0a9e4304f-european-sales-representative";
	private static Document document;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(SITE_URL).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}

	@Test
	public void testSummaryPage() throws IOException {
		Elements jobRowEl = document.getElementsByClass("position transition");
		for (Element el : jobRowEl) {
			Elements jobUrl = el.getElementsByTag("a");
			System.out.println("Job URL: " + jobUrl.attr("href"));
			Elements jobTitle = el.getElementsByTag("h2");
			System.out.println("Job Title: " + jobTitle.text());
		}
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements spec = document.getElementsByClass("description");
		System.out.println(spec.get(0).text());
	}
}
