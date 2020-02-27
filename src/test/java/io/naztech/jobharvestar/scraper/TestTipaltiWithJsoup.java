package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTipaltiWithJsoup {
	private static String url = "https://tipalti.com/careers/";
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
		Elements jobDivEl = document.getElementsByClass("custom-link btn border-width-0 btn-color-dfgh btn-flat btn-icon-left");
		System.out.println("Total Jobs: " + jobDivEl.size());
	}
	
	@Test
	public void testSummaryPage() throws IOException {
		Elements jobDivEl = document.getElementsByClass("custom-link btn border-width-0 btn-color-dfgh btn-flat btn-icon-left");
		List<String> links = new ArrayList<String>();
		for (Element element : jobDivEl) {
			String jobUrl = element.getElementsByTag("a").attr("href");
			links.add(jobUrl);	
		}
		
		for (String string : links) {
			document = Jsoup.connect(string).get();
			Elements jobUrl = document.getElementsByClass("pos-top pos-center align_left column_parent col-lg-6 boomapps_vccolumn single-internal-gutter").select("li");
			for (Element element : jobUrl) {
				String jobLinks = element.getElementsByTag("a").attr("href");
				String title = element.getElementsByTag("a").text();
				System.out.println(title);
				System.out.println(jobLinks);
			}
		}
	}
	
	@Test
	public void testDetailsPage() throws IOException {
		
		document = Jsoup.connect("https://tipalti.com/careers/sales-director-2/").get();
		Elements spec = document.getElementsByClass("uncode_text_column").select(" > *");	
		String details = "";
		for(int i=0; i<spec.size()-6;i++) {
			if(i==0) System.out.println("title: " + spec.get(i).text().trim());
			else if(i==1) System.out.println("location: "+ spec.get(i).text().trim());
			else details+= spec.get(i).text().trim()+"\n";
		}
		System.out.println("Details: " + details);
	}

}
