package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MediaMathTestJsoup {
	private static String url = "http://www.mediamath.com/careers/open-positions/";
	private static Document document;
	int pageCount;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(url).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}

	@Test
	public void testFirstPage() {
		// System.out.println(document.html());
		Elements rowElements = document.select("td.job-title");
		for (Element element : rowElements)  System.out.println("Title: " + element.child(0).attr("href"));	
	}
	@Test
	public void testGetJobDetails() throws IOException {
		// System.out.println(document.html());
		document = Jsoup.connect("http://www.mediamath.com/careers/open-positions/account-manager/4204325002/?gh_jid=4204325002").get();
		Elements title = document.select("h2");
		System.out.println(title.text());
		Elements el = document.select("h4 > span");
		System.out.println("Category: "+el.get(0).text());
		System.out.println("Location: "+el.get(1).text());
		System.out.println("Posted Date: "+el.get(2).text());
		Elements spec = document.select("div.row");
		System.out.println("Spec: "+ spec.text().replace("Share:", ""));
		
		
		
	}

}
