package io.naztech.jobharvestar.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestMicrosoftWithJsoup {
	private static String url = "https://careers.microsoft.com/us/en/job/434874/Principal-Service-Engineer";
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
	public void test() {
		System.out.println(document.html());
		Elements jobEl= document.select("span.lable-text");
		System.out.println(jobEl.size());
	}

}
