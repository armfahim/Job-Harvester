package io.naztech.jobharvestar.scraper;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test ShopClues jobs site parsing using jsoup.
 *  
 * @author rafayet.hossain
 * @since 2019-04-01
 */


public class TestCircleUpJsoup extends TestAbstractScrapper{

	private static String jobUrl = "https://circleup.com/jobs/#job-listings";
	private static Document document;
	private static String baseUrl = "https://circleup.com";
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
		Elements el = document.select("div[class=col col-md-4 col-job]");
		System.out.println(el.size());
		
	}
	
	@Test
	public void getFirstPage() {
		Elements el = document.select("div[class=col col-md-4 col-job]");
		for (int i = 0; i < el.size(); i++) 
		{
			
			Elements title = el.get(i).select("h6[class=job__title]");
			System.out.println(title.text());
			Elements location = el.get(i).select("h6[class=job__location]");
			System.out.println(location.text());
			Elements DetailsPageUrl = el.get(i).select("a[class=job d-block]");
			System.out.println(baseUrl + DetailsPageUrl.attr("href"));
			
		}

	
		
	}
	
	
	
	@Test
	public void getJobDetails() throws IOException {
		String detailsUrl = "https://circleup.com/job/cu112/";
		document = Jsoup.connect(detailsUrl).get();
		
		Elements el = document.select("div[class=job__full-description]");
		System.out.println(el.text());
		
		Elements applyUrl = document.select("a[class=btn btn-primary btn-apply]");
		System.out.println(applyUrl.attr("href"));
	
		
		
		
		
		
	}
	

}
