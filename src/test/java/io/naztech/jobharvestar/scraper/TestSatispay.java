package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import io.naztech.talent.model.Job;
import oracle.net.aso.e;

public class TestSatispay{
	private static final String SITE = "https://satispay.breezy.hr/";
	private static String baseUrl = "https://satispay.breezy.hr";
	
	@Test
	public void testGetSummuryPages(){

		Document doc;
		try {
			
				doc = Jsoup.connect(SITE).get();
				//System.out.println(doc.text());
				Elements el = doc.select("li[class=position transition]");	
			
				//body > div.body-wrapper.landing > div.container.positions > div > div > ul:nth-child(3) > li > a > h2
				for (Element element : el) {
					Job job = new Job();
					job.setTitle(element.select("a > h2").first().text());
					job.setName(job.getTitle());
					job.setLocation(element.select("a > ul > li.location > span").first().text());
					job.setUrl(baseUrl+element.select("a").attr("href"));
					String url = baseUrl+element.select("a").attr("href");
					testGetjobDetails(url,job);
					//System.out.println(job);
				}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	public void testGetjobDetails(String url,Job job) {
		 Document doc;
		try {
				doc=Jsoup.connect(url).get();
				;
				
					job.setSpec(doc.select("div[class=description]").first().text());
					job.setApplicationUrl(baseUrl+doc.select("#description > div > div.apply-container > ul > li:nth-child(1) > a").first().attr("href").toString());
					System.out.println(job);
				
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
