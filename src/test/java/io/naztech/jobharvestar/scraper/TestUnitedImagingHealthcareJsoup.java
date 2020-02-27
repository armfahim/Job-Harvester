package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestUnitedImagingHealthcareJsoup {
	private static String url = "https://usa.united-imaging.com/careers/";
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
	public void totalJob() {
		Elements totalJob = document.getElementsByClass("_text b-pagination__qty");
		System.out.println(totalJob.text());
	}
	
	@Test
	public void testGetJobDetails() throws IOException {
		Element jobDivEl = document.getElementsByClass("l-career__item _flex").get(0);

			String jobUrl = jobDivEl.getElementsByTag("a").attr("href");
			document = Jsoup.connect(jobUrl).get();
			Elements title = document.getElementsByClass("s-position__title _top");
			System.out.println(title.text());
			Elements location = document.getElementsByClass("s-position__subtitle");
			System.out.println(location.text());
			Elements spec = document.getElementsByClass("_wysiwyg");
			System.out.println(spec.text());
			}		
	}

