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
 * URL: https://goeasy.talentnest.com/en?page=0
 * 
 * @author a.s.m. tarek
 * @since 2019-03-12
 */

public class TestFundBoxJsoup {
	private static String url = "https://fundbox.com/careers/";
	private static String DETAILPAGEURL = "https://fundbox.com/job-sf?job_id=1040095";
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
		Elements test = document.select("div.tag-line");
		System.out.println(test.text());
	}

	@Test
	public void testJobRow() {
		Elements jobRowEl = document.select("div.jobs-data-container sf").select("div");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements spec = document.select("div.jobs-data-description");
		System.out.println(spec.text());
	}

}

