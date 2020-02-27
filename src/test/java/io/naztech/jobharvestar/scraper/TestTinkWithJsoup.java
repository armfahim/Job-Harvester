package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTinkWithJsoup {
	private static String baseUrl = "https://jobs.tink.se";
	private static String url = "https://jobs.tink.se/jobs";
	private static Document document;
	int totalJob = 0;

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
		Elements jobDivEl = document.getElementsByClass("title u-link-color u-no-hover");
		System.out.println("Total Jobs: " + jobDivEl.size());
	}
	
	@Test
	public void testJobDetails() throws IOException {
		Elements jobDivEl = document.select("div.jobs-section-inner >div > ul > li");
		for (Element element : jobDivEl) {

			String jobUrl = baseUrl + element.getElementsByTag("a").attr("href");
			
			document = Jsoup.connect(jobUrl).get();
			System.out.println(document.select("h1.u-primary-text-color").text());
			String location[] = document.getElementsByClass("byline u-primary-text-color").text().split("–");

			
			if(location.length > 1) {
				System.out.println("Catagory: " + location[0] );
				System.out.println("Location: " + location[1] );
			}
			else { System.out.println("Location: " + location[0]); }
			
			System.out.println(document.select("div.apply  > a").attr("href")); 
			System.out.println(document.getElementsByClass("body u-margin-top--medium u-primary-text-color").text());	
		}
	}
}
