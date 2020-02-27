package io.naztech.jobharvestar.scraper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;


public class TestCapitolis  extends TestAbstractScrapper{

	private static final String SITE = "https://www.capitolis.com/careers/";
	private static WebClient CLIENT = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}
	
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("/html/body/div[5]/div[2]/div[1]/div/div[1]/section/div/div");
		List<HtmlElement> tmp = page.getByXPath("//div[@class='col-sm-8 left job_desc_holder']");
		
		
		for (int i=0; i<el.size();i++) {
			Job job= new Job();
			job.setTitle((((DomNode) el.get(i).getByXPath("div/div[@class='job_heading']").get(0)).getTextContent()));
			job.setLocation(((HtmlElement) el.get(i).getByXPath("div/div[@class='job_location']").get(0)).getTextContent());
			job.setSpec( ((HtmlElement)tmp.get(i).getFirstByXPath("div[@class='job_content']")).getTextContent() );

			System.out.println(job);
		}
	}
	
	
	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		System.out.println(page.asText());
		
	}
	

}
