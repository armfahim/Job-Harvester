package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * URL: https://www.behaviosec.com/careers/
 * 
 * @author sohid.ullah
 * @since 2019.03.27
 * 
 **/
public class TestBehaviosecJSoup {
	private static String url = "https://www.bread.org/careers";
	private static Document document;
	private static final String JOB_DETAILS_URL = "https://www.bread.org/job/project-manager-strategic-communications-and-church-engagement";

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
		//System.out.println(document);
		
		Elements jobE = document.select("div.div.c-card-inner>a");
		System.out.println(jobE.size());
	}

	@Test
	public void testDetailPage() throws IOException {
		// document = Jsoup.connect(DETAILPAGEURL).get();

		document = Jsoup.connect(JOB_DETAILS_URL).get();

		String jobDesc = document.select("div[class=field field-name-body]").text();
		System.out.println(jobDesc);

	//	String applyUrl = document.select("div[class=field field-name-body]>p>a").attr("href");

	}

}
