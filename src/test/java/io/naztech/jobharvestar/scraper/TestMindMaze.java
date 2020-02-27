package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestMindMaze extends TestAbstractScrapper{
	private static final String SITE = "https://www.mindmaze.com/work-with-us/";
	private static WebClient CLIENT = null;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void test() throws IOException {

	}


	@Test
	public void testDates() {
		
	}

	@Test
	public void testGetJobList() {
		
	}

	@Test
	public void testFirstPage() throws IOException {
		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		
		List<String> allJobLink = new ArrayList<>(); 
		List<String> allJobCate = new ArrayList<>();
		
		List<HtmlElement> listEven = page.getBody().getElementsByAttribute("tr", "class", "srJobListJobEven");
		List<HtmlElement> listOdd = page.getBody().getElementsByAttribute("tr", "class", "srJobListJobOdd");
		
		for(int i=0; i<listEven.size(); i++) {
			allJobLink.add(listEven.get(i).getAttribute("onclick"));
			allJobCate.add(listEven.get(i).getElementsByTagName("td").get(1).asText());
		}
		for(int i=0; i<listOdd.size(); i++) {
			allJobLink.add(listOdd.get(i).getAttribute("onclick"));
			allJobCate.add(listOdd.get(i).getElementsByTagName("td").get(1).asText());
		}

		for(int i=0; i<allJobLink.size(); i++) System.out.println(allJobLink.get(i).substring(13, allJobLink.get(i).length()-3)+" "+allJobCate.get(i));
	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://www.smartrecruiters.com/MindMaze/743999684736372-business-development-and-strategy-advisor-apac";

		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);

		System.out.println("Job Title: "+ page.getBody().getOneHtmlElementByAttribute("h1", "class", "job-title").asText());
		System.out.println("Location: "+page.getBody().getOneHtmlElementByAttribute("li", "itemprop", "jobLocation").asText());
		System.out.println("Type: "+page.getBody().getOneHtmlElementByAttribute("li", "itemprop", "employmentType").asText());
		System.out.println("Job Description: "+ page.getBody().getOneHtmlElementByAttribute("section", "id", "st-jobDescription").asText());
		System.out.println("Job setPrerequisite: "+ page.getBody().getOneHtmlElementByAttribute("section", "id", "st-qualifications").asText());
	}


}
