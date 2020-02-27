package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestQapitalWithJsoup {
	private static String baseurl = "https://qapital.teamtailor.com";
	private static String url = "https://qapital.teamtailor.com/jobs";
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
		Elements jobDivEl = document.select("div.jobs-section-inner").select("ul").select("li");
		System.out.println(jobDivEl.size());
	}
	
	@Test
	public void testNextPage() throws IOException {
		Elements jobDivEl = document.select("div.jobs-section-inner").select("ul").select("li");
		for (Element element : jobDivEl) {
			String jobUrl = baseurl + element.getElementsByTag("a").attr("href");
			System.out.println(jobUrl);
			testGetJobDetails(jobUrl);
		}
	}
	
	public void testGetJobDetails(String link) throws IOException {
		document = Jsoup.connect(link).get();
		Elements jobDetails = document.getElementsByClass("body u-margin-top--medium u-primary-text-color");
		System.out.println(jobDetails.text());
	}
}
