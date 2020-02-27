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

public class TestDoorDashJsoup {
	private static String url = "https://www.doordash.com/careers/";
	private static String DETAILPAGEURL = "https://boards.greenhouse.io/doordash/jobs/1567070";
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
		//System.out.println(document.html());
		Elements test = document.select("p.sc-cugefK dJijCY sc-gVLVqr emiNhg");
		System.out.println(test.text());
	}

	@Test
	public void testJobRow() {
		Elements jobRowEl = document.select("ul.sc-fjhmcy").select("a.sc-jdeSqf aAMgj Button_root___1Fnwf");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements title = document.select("h1.app-title");
		System.out.println(title.text());
	}

}

