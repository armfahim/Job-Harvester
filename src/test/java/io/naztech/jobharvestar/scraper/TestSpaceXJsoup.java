package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * SpaceX job site scraper. <br>
 * URL: https://www.spacex.com/careers/list
 *
 * @author a.s.m. tarek
 * @since 2019-03-11
 */

public class TestSpaceXJsoup {
	private static String url = "https://www.spacex.com/careers/list";
	private static String DETAILPAGEURL = "https://boards.greenhouse.io/spacex/jobs/4138793002?gh_jid=4138793002";
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
		Elements jobRowEl = document.select("div.view-content").select("tbody").select("tr");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements title = document.select("h1.app-title");
		System.out.println(title.text());
	}

}
