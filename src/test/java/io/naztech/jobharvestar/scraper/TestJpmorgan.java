package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestJpmorgan {
	
	private static final String URL = "https://jobs.jpmorganchase.com/ListJobs/All";
	private static Connection con;
	private static Document doc;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		con = Jsoup.connect(URL);
		doc = con.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		Elements listE = doc.select("table.JobListTable > tbody > tr:not(:has(th))");
		assertEquals(31, listE.size());
	}
	
	@Test
	public void testTitle() {
		String title = doc.select("table.JobListTable > tbody > tr:not(:has(th))").get(1).child(1).child(0).text();
		assertEquals("Associate Banker - Part time - Kaliste Saloom - Lafayette, LA", title);
	}
	

}
