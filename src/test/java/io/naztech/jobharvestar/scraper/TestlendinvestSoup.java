package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Lendinvest jobsite parse <br>
 * url: https://www.lendinvest.com/careers/careers-open-positions/
 * @author sohid.ullah
 * @since 2019-03-27
 * 
 **/
public class TestlendinvestSoup {
	private static String url = "https://www.lendinvest.com/careers/careers-open-positions/";
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

		Elements jobE = document.select("div.vc__careers-list__job__link__wrapper>a");

		String jobUrl = jobE.get(0).attr("href"); // First Job URL
	}

	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect("https://lendinvest.workable.com/j/F1A0D48F80").get();

		String jobTitle = document.select("section.section>h1").text();

		// System.out.println(document.select("section.section>p").get(1).text());

		String location[] = document.select("section.section>p").get(1).text().split("·");
		// System.out.println(location[0]);

		String jobDetails = document.select("section.section").get(1).text();
		
		String applyUrl = document.select("section.section").get(2).select("a").attr("href");

	}

}
