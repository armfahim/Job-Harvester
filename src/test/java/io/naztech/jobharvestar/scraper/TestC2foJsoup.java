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
 * C2FO job site scraper. <br>
 * URL: https://c2fo.com/company/careers/
 * 
 * @author muhammad tarek
 * @since 2019-03-27
 */
public class TestC2foJsoup {
	private static String url = "https://c2fo.com/company/careers/";
	private static String DETAILPAGEURL = "https://c2fo.com/indeed-jobs/c766f850df8c92c13f10/global-crm-salesforce-administrator/";
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
	public void testSummaryPage() throws IOException {
		Elements jobRowEl = document.getElementsByClass("elementor-text-editor elementor-clearfix").get(1)
				.getElementsByTag("a");
		for (Element el : jobRowEl) {
			Elements jobUrl = el.getElementsByTag("a");
			System.out.println(jobUrl.attr("href"));
			System.out.println(jobUrl.text());

		}
	}

	@Test
	public void testJobRow() {
		Elements jobRowEl = document.getElementsByClass("elementor-text-editor elementor-clearfix").get(1)
				.getElementsByTag("a");
		System.out.println(jobRowEl.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements spec = document.getElementsByClass("entry-content");
		System.out.println(spec.get(0).text());

		for (Element el : spec) {
			Elements loc = el.getElementsByTag("p");
			System.out.println(loc.get(0).text());
		}
	}

}
