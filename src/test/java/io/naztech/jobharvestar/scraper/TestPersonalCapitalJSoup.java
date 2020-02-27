package io.naztech.jobharvestar.scraper;
import static org.junit.Assert.*;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import io.naztech.talent.model.Job;

public class TestPersonalCapitalJSoup {

	private static final String SITE = "https://personalcapital.applytojob.com";
	@Test
	
	public void testgetSummaryPage() {
		Document doc = null;
		try 
		{
			doc = Jsoup.connect(SITE).get();
			Elements e=doc.select("h4[class=list-group-item-heading]");
			
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
			Element jobSec = doc.select("body").first();
			Job j = new Job();
			j.setTitle(e.text());
			j.setName(j.getTitle());
			j.setUrl(url);
			j.setApplicationUrl(j.getUrl());		
			j.setLocation(jobSec.select("li[title=Location]").first().text());
			j.setSpec(jobSec.select("div[class=description]").first().text());
			System.out.println(j+"\n\n");
		} 
		catch (IOException iox) 
		{
			
			iox.printStackTrace();
		}
	}
}