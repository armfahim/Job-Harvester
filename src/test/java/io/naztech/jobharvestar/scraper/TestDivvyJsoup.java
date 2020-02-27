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

/**
 * Test ShopClues jobs site parsing using jsoup.
 *  
 * @author rafayet.hossain
 * @since 2019-03-31
 */


public class TestDivvyJsoup extends TestAbstractScrapper{

	private static String jobUrl = "https://usr55.dayforcehcm.com/CandidatePortal/en-US/motivate/site/divvycareers";
	private static Document document;
	private static String baseUrl = "https://usr55.dayforcehcm.com";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(jobUrl).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}
	
	@Test
	public void getJobList() {
		Elements el = document.select("li[class=search-result]");
		System.out.println(el.size());
		
	}
	
	@Test
	public void getFirstPage() {
		Elements el = document.select("li[class=search-result]");
		for (int i = 0; i < el.size(); i++) 
		{
			
			Elements title = el.get(i).select("div[class=posting-title]");
			System.out.println(title.text());
			Elements location = el.get(i).select("div[class=location]");
			System.out.println(location.text());
			Elements postDate = el.get(i).select("div[class=posting-date]");
			
			System.out.println(postDate.text().split(",")[1] + postDate.text().split(",")[2]);
			
			Elements applyUrl = el.get(i).select("div.posting-actions>a");
			System.out.println(baseUrl + applyUrl.attr("href"));
			
			
			Elements DetailsPageUrl = el.get(i).select("div.posting-description>a");
			System.out.println(baseUrl + DetailsPageUrl.attr("href"));
			
		}

	
		
	}
	
	
	
	@Test
	public void getJobDetails() throws IOException {
		String detailsUrl = "https://usr55.dayforcehcm.com/CandidatePortal/en-US/motivate/site/divvycareers/Posting/View/3176";
		document = Jsoup.connect(detailsUrl).get();
		
		Elements el = document.select("div[class=job-posting-content]");
		System.out.println(el.text());
	
		
		
		
		
		
	}
	

}
