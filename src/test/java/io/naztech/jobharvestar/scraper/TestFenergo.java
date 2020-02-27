package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFenergo {
	
	public static final String SITE = "https://www.fenergo.com/company/careers/current-job-opportunities/";
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
	public void testGetJobList() {
		baseUrl=SITE.substring(0,23);
		Elements jobUrl = document.select(".holder");
		for (Element job : jobUrl) {
			System.out.println("LOCATION: "+job.selectFirst(".location").text().trim());
			System.out.println("TITLE: "+job.getElementsByTag("a").first().text().trim());
			System.out.println("URl: "+baseUrl+job.getElementsByTag("a").first().attr("href").trim());
		}
	}
	
	@Test
	public void getJobDetails() throws IOException {
		String link="https://www.fenergo.com/company/careers/current-job-opportunities/career.html?job=049C148CCF";
		document=Jsoup.connect(link).get();
		Element postDate=document.getElementsByClass("date-inline title-bold").first();
		Element apply=document.getElementsByClass("btn-holder text-center").first();
		Element overview=document.getElementsByClass("title title-bold title-small black title-word-spacing-big no-bottom-indent").first();
		Elements desc=document.select(".manual-post-body > *");
		
		String description=overview.text().trim()+"\n\n";
		for (Element element : desc) {
			description+=element.text().trim()+"\n";
		}
		System.out.println("POST DATE: "+postDate.text().split(":")[1].trim());
		System.out.println("APPLy URL: "+baseUrl+"/"+apply.child(0).attr("href").trim());
		System.out.println(description);
	}

}
