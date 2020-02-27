package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * URL: https://brighte.com.au/careers/
 * 
 * @author sohid.ullah
 * @since 25.03.19
 * 
 **/
public class TestBreadJSoup {
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

		Elements jobE = document.select("div>h2>a");

		//System.out.println(jobE.size());
		// System.out.println(jobE.get(1).attr("href")); String title =
		
		String title = jobE.get(1).text().trim();
		System.out.println(title);
//		String jobUrl = jobE.get(1).attr("href");
//		
//		String baseUrl = url.substring(0, 21);
		//System.out.println(baseUrl + jobUrl);

	}

	@Test
	public void testDetailPage() throws IOException {
		// document = Jsoup.connect(DETAILPAGEURL).get();

		document = Jsoup.connect(JOB_DETAILS_URL).get();

		String jobDesc = document.select("div[class=field field-name-body]").text();
		System.out.println(jobDesc);

//		String applyUrl = document.select("div[class=field field-name-body]>p>a").attr("href");

	}

}
