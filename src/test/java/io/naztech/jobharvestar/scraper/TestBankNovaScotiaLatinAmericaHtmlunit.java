package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestBankNovaScotiaLatinAmericaHtmlunit extends TestAbstractScrapper{
	private static final String SITE = "https://empleos.scotiabank.com/";
	private static WebClient CLIENT = null;

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM. dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM. d, yyyy");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testDates() throws IOException {
		String Link = "https://empleos.scotiabank.com/trabajo/lima/funcionario-de-negocios-y-servicios-volante-lima-zona-centro/12568/3498150";

		HtmlPage page = CLIENT.getPage(Link);
		System.out.println("Job Date: "+ page.getBody().getOneHtmlElementByAttribute("span", "class", "job-date job-info").asText());
		System.out.println("Date : "+parseDate(page.getBody().getOneHtmlElementByAttribute("span", "class", "job-date job-info").asText(), DF,DF2));
	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		
		List<HtmlElement> jobLinks = page.getBody().getByXPath("//section[@class = 'job-list job-list-container']/ul/li/a");
		for(int i=0; i<jobLinks.size(); i++) System.out.println(SITE + jobLinks.get(i).getAttribute("href"));
 	}

	@Test
	public void testGetNextPage() throws InterruptedException {
	
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://empleos.scotiabank.com/trabajo/lima/funcionario-de-negocios-y-servicios-volante-lima-zona-centro/12568/3498150";

		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);

		
		System.out.println("Job Title: "+ page.getBody().getOneHtmlElementByAttribute("section", "class", "job-description").getElementsByTagName("h1").get(0).asText());
		System.out.println("Job ApplicationUrl: "+ page.getBody().getOneHtmlElementByAttribute("a", "class", "button job-apply top").getAttribute("href"));
		
		System.out.println("Job ID: "+ page.getBody().getOneHtmlElementByAttribute("span", "class", "job-id job-info").asText());
		System.out.println("Job Date: "+ page.getBody().getOneHtmlElementByAttribute("span", "class", "job-date job-info").asText());
		System.out.println("Job Location: "+ page.getBody().getOneHtmlElementByAttribute("span", "class", "job-loc job-info").asText());
		
		System.out.println("Job Description: "+ page.getBody().getOneHtmlElementByAttribute("div", "class", "ats-description").asText());
		
	}
}