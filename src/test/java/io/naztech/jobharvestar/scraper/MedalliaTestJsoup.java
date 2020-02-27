package io.naztech.jobharvestar.scraper;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Medallia Jobsite Parser<br>
 * URL: https://jobs.medallia.com/
 * 
 * @author Fahim Reza
 * @since 2019-03-12
 */

public class MedalliaTestJsoup {
	private static String url = "https://jobs.medallia.com";
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
		System.out.println(document.title());
		Elements jobRow = document.select("ul.all-locations");
		System.out.println(jobRow.size());
		for (Element el : jobRow) {
			System.out.println("Title: " + el.getElementsByTag("li").get(0).text());
			System.out.println("URL: " + el.getElementsByTag("a").attr("href"));
			System.out.println("Category: " + el.getElementsByTag("li").get(1).text());
			System.out.println("Location: " + el.getElementsByTag("li").get(2).text());
		}
	}

	@Test
	public void testGetDetails() throws IOException {
		document = Jsoup.connect("https://jobs.medallia.com/job/account-director-london/d0e40313").get();
		Elements type = document.select("span.j-commitment");
		Elements spec = document.select("div.j-description > div");
		System.out.println("Spec: " + spec.size());
		System.out.println("Type: " + type.text());

	}

}
