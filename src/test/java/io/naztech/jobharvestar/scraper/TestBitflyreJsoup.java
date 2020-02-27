package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.naztech.talent.model.Job;

public class TestBitflyreJsoup {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	
	void getJobDetails(Element e) {
		
		String baseUrl="https://bitflyer.com/en-jp";
		String url = baseUrl+e.select("a").first().attr("href");
		
		//System.out.println(url);
		
		try {
			Document doc = Jsoup.connect(url).get();
			
			Element jobSec = doc.select("#top > main > section.section.offer-content").first();
			Job j = new Job();
			
			j.setTitle(e.text());
			j.setName(j.getTitle());
			j.setUrl(url);
			
			
			j.setApplicationUrl(baseUrl+jobSec.select("div.offer-content__entry-button-wrapper > a.recruit-btn").first().attr("href"));
			
			
			String spec="";
			for (Element element : jobSec.select("div[class=offer-content__group]")) {
				
				if(element.select("h1").first().text().toLowerCase().contains("job location")) {
					
					j.setLocation(element.text());
					
				}else {
					//System.out.println(element.text());
					spec+=element.text();
				}
				
			}
			
			j.setSpec(spec);
			
			System.out.println(j);
			
			
		} catch (IOException iox) {
			
			iox.printStackTrace();
		}
		
	}

	@Test
	public void getSummaryPages() {
		
		try {
			Document doc = Jsoup.connect("https://bitflyer.com/en-jp/recruit").get();
			
			Elements titleListE = doc.select("li[class=positions__item]");
			
			for (Element element : titleListE) {
				
				System.out.println(element.text());
				getJobDetails(element);
				
			}
		} catch (IOException e) {
			
		}
		
	}
	
	
}
