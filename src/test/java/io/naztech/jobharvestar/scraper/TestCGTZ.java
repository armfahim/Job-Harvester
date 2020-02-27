package io.naztech.jobharvestar.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCGTZ extends TestAbstractScrapper{

	private static final String SITE = "https://www.cgtz.com/about/takeJob.html";
	private static Document document;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document=Jsoup.connect(SITE).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}

	@Test
	public void testGetJobList() {
		Elements jobHeader = document.getElementsByClass("questionText  closeArrow ");
		Elements jobDesc = document.getElementsByClass("answerText disNone");
		Element jobHeaderFirst = document.getElementsByClass("questionText  openArrow ").first();
		Element jobDescFirst = document.selectFirst(".answerText");
		
		System.out.println("TITLE: "+jobHeaderFirst.text().trim());
		System.out.println("DESCRIPTION: "+jobDescFirst.text().trim());
		
		for (int i = 0; i < jobHeader.size(); i++) {
			System.out.println("TITLE: "+jobHeader.get(i).text().trim());
			System.out.println("DESCRIPTION: "+jobDesc.get(i).text().trim());
		}
	}
}
