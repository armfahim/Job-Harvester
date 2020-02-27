package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Policy Bazaar job site scraper. <br>
 * URL: https://www.policybazaar.com/careers/
 * 
 * @author a.s.m. tarek
 * @since 2019-03-13
 */
public class TestPolicyBazaarJsoup {
	private static String url = "https://www.policybazaar.com/careers/";
	private static String DETAILPAGEURL = "https://www.policybazaar.com/careers/";
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
		Elements test = document.select("h.careerHeading");
		System.out.println(test.text());
	}

	@Test
	public void testJobRow() {
		Elements jobRowEl = document.select("div.openingsBlock");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements spec = document.select("div.posInfo fLeft fullWidth").select("ul.reqList");
		System.out.println(spec.text());
	}

}
