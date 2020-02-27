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

public class TestNerdWalletJsoup{
	private static final String SITE = "https://www.nerdwallet.com/careers?trk=nw_gf_5.0";
	private static String baseUrl = "https://www.nerdwallet.com";
	
	@Test
	public void testGetSummaryPage() {
		 Document doc;
		try {
				doc=Jsoup.connect(SITE).get();
				
				Elements jobUrl = doc.select("a[class=departments__tile]");
				for (Element element : jobUrl) {
					testgetJobDetails(baseUrl+element.attr("href"));
					
				}
				
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testgetJobDetails(String url) {
		
		try {
				Document doc =Jsoup.connect(url).get();
				Elements jobList = doc.select("div[class=joblist__tile--cont]");
				for (Element element : jobList) {
					Job job = new Job();
					job.setTitle(element.select("p[class=joblist__header _1oz19 _2d7ND]").text());
					job.setName(job.getTitle());
					job.setLocation(element.select("p[class=joblist__jobs _2t0Jv]").text());
					job.setUrl(baseUrl+element.select("div[class=joblist__tile--cont]>a").attr("href"));
					testGetJobSpec(baseUrl+element.select("div[class=joblist__tile--cont]>a").attr("href"), job);
				}
				
			
		}catch (Exception e) {
			
		}
	}
	public Job testGetJobSpec(String url,Job job) 
	{
		try 
		{
			Document doc =Jsoup.connect(url).get();
			
			job.setSpec(doc.select("div[class=job__cont _2m6fE]").text());
			job.setApplicationUrl(job.getUrl()+doc.select("div[class=job__hdr--apply-mobile _23e1j _1SRCr KH8lp nVuqt]>a").attr("href"));
			System.out.println(job);
			return job;
			
		}
		catch (Exception e) 
		{
			
		}
		return null;
	}
}

