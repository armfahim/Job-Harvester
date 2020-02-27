package io.naztech.jobharvestar.scraper;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Abstract Workable jobs site parsing using jsoup.
 *  
 * @author rafayet.hossain
 * @since 2019-03-31
 */


public class TestAbstractWorkable extends TestAbstractScrapper{

	private static String jobUrl = "https://bitpesa.workable.com/";
	private static Document document;
	//private static String baseUrl = "";
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
		Elements el = document.select("li[class=job]");
		System.out.println(el.size());
		
	}
	
	@Test
	public void getFirstPage() {
		Elements el = document.select("li[class=job]");
		for (int i = 0; i < el.size(); i++) 
		{
			
			Elements title = el.get(i).select("li.job>h2>a");
			System.out.println(title.text());
			Elements location = el.get(i).select("p.meta");
			System.out.println(location.text().split("·")[0]);
			System.out.println("Catagory:" + location.text().split("·")[1]);
			
			//Elements detailsPageUrl = el.get(i).select("li.job>h2>a");
			System.out.println("Details Page Url: " + jobUrl + title.attr("href"));
			
			
			
		}

	
		
	}
	
	
	
	@Test
	public void getJobDetails() throws IOException {
		//String detailsUrl = "https://bitpesa.workable.com/j/F47B21B32C?viewed=true";
		String detailsUrl = "https://bitpesa.workable.com/j/652DA2A94B?viewed=true";
		document = Jsoup.connect(detailsUrl).get();
		
		Element description = document.select("section[class=section section--text]").get(0);
		//Element otherDes = document.select("section[class=section section--text]").get(2);
		//System.out.println(description.text() + " " + otherDes.text());
		System.out.println(description.text());
		Element prequisit = document.select("section[class=section section--text]").get(1);
		System.out.println(prequisit.text());
	
		Elements applicationUrl = document.select("a[class=btn btn--primary btn--large]");
		System.out.println(applicationUrl.attr("href"));
		
		
		
		
		
	}
	

}
