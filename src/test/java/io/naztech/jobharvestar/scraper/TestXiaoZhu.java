package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestXiaoZhu extends TestAbstractScrapper{
	private static final String SITE = "http://xiaozhu.zhiye.com/alljob/?o=2";
	private String baseUrl = null;
	private static WebClient CLIENT = null;

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void test() throws IOException {

	}


	@Test
	public void testDates() throws IOException {
		String Link = "http://xiaozhu.zhiye.com/zpdetail/270129960?o=2";

		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		
		List<HtmlElement> list = page.getBody().getElementsByAttribute("li", "class", "nvalue");
		System.out.println("List Size = "+list.size());
		System.out.println("Date: "+list.get(4).asText());
		System.out.println(parseDate(list.get(4).asText(), DF));
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		this.baseUrl = SITE.substring(0, 24);
		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		
		List<HtmlElement> list = page.getBody().getElementsByTagName("a");
		
		for(int i=0; i<list.size(); i++) {
			String Link = list.get(i).getAttribute("href");
			if(Link.contains("/zpdetail/")) {
				System.out.println(baseUrl+Link);
			}
		}
	}

	@Test
	public void testFirstPage() throws IOException {

		this.baseUrl = SITE.substring(0, 24);
		
		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		System.out.println(page.getBody().getOneHtmlElementByAttribute("div", "class", "counts").asText());
		String parts = page.getBody().getOneHtmlElementByAttribute("div", "class", "counts").asText().substring(1, 3);
		int totalJob = Integer.parseInt(parts.trim());
		System.out.println("Total job = "+totalJob);
		int totalPage = totalJob/15;
		for(int i=2; i<= totalPage; i++) {
			System.out.println(baseUrl+"/alljob/?o=2&PageIndex="+i);
		}
	}

	@Test
	public void testGetNextPage() throws IOException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "http://xiaozhu.zhiye.com/zpdetail/270129960?o=2";

		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		
		List<HtmlElement> list = page.getBody().getElementsByAttribute("li", "class", "nvalue");
		System.out.println("List Size = "+list.size());
		for(int i=0; i<list.size(); i++) System.out.println("i = "+i+" "+list.get(i).asText());

		System.out.println("Cate: "+list.get(0).asText());
		System.out.println("Type: "+list.get(1).asText());
		System.out.println("Salary: "+list.get(2).asText());
		System.out.println("Date: "+list.get(0).asText());
		
		System.out.println("Job Title: "+ page.getBody().getOneHtmlElementByAttribute("div", "class", "boxSupertitle").asText());
		System.out.println("Location: "+ page.getBody().getOneHtmlElementByAttribute("li", "class", "nvcity").asText());
		System.out.println("Job Description: "+ page.getBody().getOneHtmlElementByAttribute("div", "class", "xiangqingtext").asText());
	}
}