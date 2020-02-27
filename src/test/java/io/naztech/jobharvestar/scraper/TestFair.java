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

public class TestFair {

	public static final String SITE = "https://www.fair.com/careers/";
	public static Document document;
	private String baseUrl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document=Jsoup.connect(SITE).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}

	@Test
	public void testGetJobList() throws IOException {
		Elements jobDept = document.select(".department-link");
		List<String> jobCategory = new ArrayList<String>();
		for (Element element : jobDept) {
			if (!element.text().trim().equalsIgnoreCase("Future Opportunities"))
				jobCategory.add(getBaseUrl()+element.attr("href").trim());
		}
		getSummaryPage(jobCategory);
	}
	
	private void getSummaryPage(List<String> jobCategory) throws IOException {
		for (String url : jobCategory) {
			document=Jsoup.connect(url).get();
			Element categoryEl = document.selectFirst("#department").getElementsByTag("h2").first();
			String category = categoryEl.text().trim();
			System.out.println("CATEGORY: "+"==========================="+category+"=======================");
			Elements jobUrl = document.select(".position-item");
			for (Element element : jobUrl) {
				System.out.println("CATEGORY: "+category);
				System.out.println("URl: "+baseUrl+element.getElementsByTag("a").attr("href").trim());
				System.out.println("TITLE: "+element.getElementsByTag("a").text().trim());
				System.out.println("LOCATION: "+element.getElementsByTag("div").text().trim());
			}
		}
	}
	
	@Test
	public void getJobDetails() throws IOException {
		String link="https://www.fair.com/careers/position/4190379002";
		document=Jsoup.connect(link).get();
		Element apply=document.getElementsByClass("btn primary").first();
		Elements desc=document.select(".position-description");
		
		System.out.println("APPLy URL: "+getBaseUrl()+apply.attr("href").trim());
		System.out.println(desc.text().trim());
	}
	
	private String getBaseUrl() {
		baseUrl=SITE.substring(0,20);
		return baseUrl;
	}
	
}
