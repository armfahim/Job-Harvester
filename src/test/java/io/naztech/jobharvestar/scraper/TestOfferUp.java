package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestOfferUp {

	private static String url = "https://about.offerup.com/careers/";
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
	public void testGetJobList() {
		Elements totalJob = document.getElementsByClass("mb-0 mt-2");
		int jobCount = 0;
		for (Element element : totalJob) {
			jobCount+=Integer.parseInt(element.text().split(" ")[0].trim());
		}
		System.out.println("Total JOB: "+jobCount);
	}
	
	@Test
	public void testGetNextPage() throws IOException {
		Elements nextPageUrl = document.getElementsByClass("col-md-4 col-xs-5");
		for (Element element : nextPageUrl) {
			Document nextPage = Jsoup.connect(element.select("a").attr("href")).get();
			categoryTotalJob(nextPage);
			//System.out.println(element.select("a").attr("href"));
		}
	}
	
	private void categoryTotalJob(Document nextPage) {
		Elements categoryElements = nextPage.select("h3.c-green");
		for (Element element2 : categoryElements) {
			System.out.println(element2.text());
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String link = "https://about.offerup.com/careers/data-science/data-scientist/";
		document = Jsoup.connect(link).get();
		Elements jobDetails = document.select("div.text-editor > *");
		for (int i = 0; i < jobDetails.size(); i++) {
			if (i==jobDetails.size()-1) {
				System.out.println("LINK: "+jobDetails.get(i).select("a").attr("href"));	
			} else {
				System.out.println(jobDetails.get(i).text());
			}
		}
	}
}
