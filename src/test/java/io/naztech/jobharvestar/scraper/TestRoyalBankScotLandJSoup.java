package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Root Insurance job site scraper. <br>
 * URL: https://www.joinroot.com/careers
 * 
 * @author sohid.ullah
 * @since 2019-04-29
 */
public class TestRoyalBankScotLandJSoup {
	private static String url = "https://jobs.rbs.com/search/jobs?q=";
	private static String DETAILPAGEURL = "https://jobs.rbs.com/search/jobs?q=";
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
		Elements test = document.select("h2.component__title font-size--lg");
		System.out.println(test.text());
	}

	@Test
	public void testJobRow() {
		Elements elTotalPages = document.select("p.search-result-count");
		double jobPerPage = Integer.parseInt(elTotalPages.get(0).text().split("of")[0].split("-")[1].trim());
		double totalJob = Integer.parseInt(elTotalPages.get(0).text().split("of")[1].trim());
		
		double totalPage = totalJob/jobPerPage;
		
		System.out.println(Math.ceil(totalPage));

	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements title = document.select("span.careerTitle__position");
		System.out.println(title.get(0).text());
	}

}
