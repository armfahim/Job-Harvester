package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * URL: https://cloudmargin.com/careers/
 * 
 * @author sohid.ullah
 * @since 25.03.19
 * 
 **/
public class TestCloudMarginJSoup {
	private static String url = "https://cloudmargin.com/careers/";
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

		Elements jobE = document.select("div.col-sm-9>div.pb4");
		
		System.out.println(jobE.size());

	}

}
