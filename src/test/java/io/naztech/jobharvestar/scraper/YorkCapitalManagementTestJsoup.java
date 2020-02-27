package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class YorkCapitalManagementTestJsoup {
	private static String baseurl = "https://www.careerbuilder.com";
	private static String url = "https://www.careerbuilder.com/jobs-york-capital-management";
	private static Document document;
	int pageCount;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(url).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}

	@Test
	public void testFirstPage() {
		//System.out.println(document.html());
		String totalJob = document.select("div.count").text();
		System.out.println(totalJob.substring(1, totalJob.length()-6)+" "+totalJob.length());
		Elements jobRow = document.select("div.job-row");
		System.out.println(jobRow.size());
		for (Element el : jobRow) {
			System.out.println("Title: " + el.getElementsByClass("job-title show-for-medium-up").text());
			System.out.println("URL: " + el.getElementsByTag("a").attr("href"));
			System.out.println("Category: " + el.getElementsByClass("columns large-2 medium-3 small-12").text());
			System.out.println("Location: " + el.getElementsByClass("columns end large-2 medium-3 small-12").text());
		}
	}

	@Test
	public void testTotalPageCount() {
		pageCount = (Integer.parseInt(document.select("div.count").text().substring(1, 4))) / 25;
		System.out.println(pageCount);

	}

	@Test
	public void testNextPage() throws IOException {
		testTotalPageCount();
		for (int i = 1; i < pageCount; i++) {
			document = Jsoup.connect("https://www.careerbuilder.com/jobs-york-capital-management?page_number=" + i)
					.get();
			Elements jobRow = document.select("div.job-row");
			System.out.println(jobRow.size());
			for (Element el : jobRow) {
				System.out.println("Title: " + el.getElementsByClass("job-title show-for-medium-up").text());
				System.out.println("URL: " + el.getElementsByTag("a").attr("href"));
				System.out.println("Category: " + el.getElementsByClass("columns large-2 medium-3 small-12").text());
				System.out
						.println("Location: " + el.getElementsByClass("columns end large-2 medium-3 small-12").text());
			}
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		testTotalPageCount();
		for (int i = 1; i < pageCount; i++) {
			document = Jsoup.connect("https://www.careerbuilder.com/jobs-york-capital-management?page_number=" + i)
					.get();
			Elements jobRow = document.select("div.job-row");
			for (Element el : jobRow) {
				String url = baseurl + el.getElementsByTag("a").attr("href");
				document = Jsoup.connect(url).get();
				Elements postedDate = document.select("h3#job-begin-date");
				System.out.println(postedDate.text().substring(7));
				Elements spec = document.select("div.description");
				System.out.println(spec.get(0).wholeText());
			}

		}
	}

}