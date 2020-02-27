package io.naztech.jobharvestar.scraper;
import static org.junit.Assert.*;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import io.naztech.talent.model.Job;

public class TestWealthfront {

	private static final String SITE = "https://www.wealthfront.com/careers";
	@Test
	
	
	public void testgetSummaryPage() {
		Document doc = null;
		try 
		{
			doc = Jsoup.connect(SITE).get();
			Elements e=doc.select("#job-board > div > div > div > div.static-guest-careers-open-positions-section > ul > li > ul > li");
			
			for (Element element : e) 
			{
		    	//System.out.println(element.text());
		    	
		    	getJobDetails(element);
			}		   
		}
		
		    catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	public void getJobDetails(Element e) 
	{
		
		String url =e.select("a").first().attr("href");
		//System.out.println(url);
		try
		{
			Document doc = Jsoup.connect(url).get();
			Element jobSec = doc.select("body > div.content-wrapper.posting-page > div").first();
			Job j = new Job();
			
			j.setTitle(e.text());
			j.setName(j.getTitle());
			j.setUrl(url);
			j.setApplicationUrl(jobSec.select("a[class=postings-btn template-btn-submit teal]").first().attr("href"));
			j.setLocation(jobSec.select("div[class=sort-by-time posting-category medium-category-label]").first().text());
			j.setSpec(jobSec.select("div[class=section-wrapper page-full-width]").first().text());
			
			
			System.out.println(j);
			
		} 
		
		catch (IOException iox) 
		{
			
			iox.printStackTrace();
		}
		
	}

}