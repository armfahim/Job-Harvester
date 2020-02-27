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
 * @author a.s.m. tarek
 * @since 2019-03-14
 */
public class TestRootInsuranceJsoup {
	private static String url = "https://www.joinroot.com/careers";
	private static String DETAILPAGEURL = "https://www.joinroot.com/careers/118b11be-36d5-4dc0-b8d1-2cfb72304678";
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
		Elements jobRowEl = document.select("a.rJobPostingListItem__cta");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements title = document.select("span.careerTitle__position");
		System.out.println(title.get(0).text());
	}

}
