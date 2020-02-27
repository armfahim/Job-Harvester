package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStocardWithJsoup {
	private static String baseurl = "https://stocardapp.com";
	private static String url = "https://stocardapp.com/en/au/jobs";
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
	public void totalJob() {
		Elements jobDivEl = document.select("span#jobs-count");
		System.out.println(jobDivEl.text());
	}
	
	@Test
	public void BrowseJobs() throws IOException {
		Elements jobDivEl = document.getElementsByClass("row full-width").select("a");
		Elements jobLoc = document.getElementsByClass("location light");
		for (Element element : jobDivEl) {
			String jobUrl = baseurl + element.getElementsByTag("a").attr("href");
			System.out.println(jobUrl);
			System.out.println(element.text());
			System.out.println(jobLoc.get(0).text());
			testGetJobDetails(jobUrl);
		}
	}
	
	public void testGetJobDetails(String link) throws IOException {
		document = Jsoup.connect(link).get();
		Elements jobSpec = document.getElementsByClass("col-md-12");
		Elements jobUrl = document.getElementsByClass("btn button-wide application-button");
		System.out.println(jobSpec.get(3).text());
		System.out.println(jobUrl.attr("href"));	
	}
}
