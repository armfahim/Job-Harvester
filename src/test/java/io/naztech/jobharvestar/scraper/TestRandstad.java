package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRandstad {
	private static String url = "https://www.randstad.com/jobs";
	private static String DETAILPAGEURL="https://www.randstad.com/jobs/ceska-republika/obsluha-vzv-ostrava-pozice-je-vhodna-i-pro-zeny-mzda-cca-22-000-kc-brutto_ostrava_16651928/";
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
	public void testTotalJob() {
		Elements totalJobEl= document.select("span#ctl06_ctl05_NrOfJobsLabel");
		System.out.println(totalJobEl.text().split("of")[1].trim());
	}
	
	@Test
	public void testJobRow() {
		Elements jobRowEl= document.select("div#ctl06_ctl05_JobResultsDiv").select("article");
		System.out.println(jobRowEl.size());
	}
	@Test
	public void testDetailPage() throws IOException {
		document = Jsoup.connect(DETAILPAGEURL).get();
		Elements applicationUrl=document.select("a#ctl06_ctl05_ApplyTopHyperLink");
		System.out.println(applicationUrl.get(0).attr("href"));
	}
	
}
