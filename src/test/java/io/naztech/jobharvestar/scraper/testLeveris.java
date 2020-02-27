package io.naztech.jobharvestar.scraper;
import static org.junit.Assert.*;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import io.naztech.talent.model.Job;

public class testLeveris {

	private static final String SITE = "https://leveris.com/careers/";
	@Test
	
	public void testgetSummaryPage() {
		Document doc = null;
		try 
		{
			doc = Jsoup.connect(SITE).get();
			Elements e=doc.select("div[class=feature feature-1 boxed boxed--border]");
			for (Element element : e) 
			{
		    	getJobDetails(element);
			}			
		}
		    catch (Exception e) 
		{
			e.printStackTrace();
		}   
	}

	public void getJobDetails(Element e) 
	{
		String url =e.select("a").first().attr("href");
		try
		{
			Document doc = Jsoup.connect(url).get();
			Element jobSec = doc.select("div[class=main-container]").first();
			Job j = new Job();
			j.setTitle(jobSec.select("li[class=active]+li").text());
			j.setName(j.getTitle());
			j.setUrl(url);
			j.setApplicationUrl(j.getUrl());		
			j.setLocation(jobSec.select("h2:has(span[style])").text());
			j.setSpec(jobSec.select("div[class=col-sm-8 col-md-7 article__body]").text());
			System.out.println(j);
		} 
		catch (IOException iox) 
		{
			
			iox.printStackTrace();
		}
	}
}