package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestInsuranceAustraliaGrpHtmlunit extends TestAbstractScrapper {
	private static final String SITE = "https://www.iagcareers.com.au/jobtools/jncustomsearch.searchResults?in_organid=15941&in_jobDate=All";
	private static WebClient CLIENT = null;
	private String baseUrl;
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void test() throws IOException {

	}

	@Test
	public void testDates() throws IOException {
		String Link = "https://www.iagcareers.com.au/jobs/IAG-1366005I";
		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(TIME_10S);

		System.out.println("Date: " + page.getBody().getOneHtmlElementByAttribute("div", "class", "postedDate col-sm-4").asText());
		System.out.println(parseDate(page.getBody().getOneHtmlElementByAttribute("div", "class", "postedDate col-sm-4").asText().trim(), DF));
	}

	@Test
	public void testGetJobList() {

	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(TIME_10S);

		this.baseUrl = SITE.substring(0, 29);

		// System.out.println(page.asText());
		// System.out.println(page.asXml());

		int totalJob = Integer.parseInt(page.getBody().getOneHtmlElementByAttribute("div", "class", "search_results")
				.getElementsByTagName("strong").get(0).asText());
		int totalPages = getPageCount(page.getBody().getOneHtmlElementByAttribute("div", "class", "search_results")
				.getElementsByTagName("strong").get(0).asText(), 20);
		System.out.println("Total Job: " + totalJob + " Total Page: " + totalPages);

		List<HtmlElement> list = page.getBody().getElementsByAttribute("a", "onmouseover",
				"window.status='View details'; return true;");

		for (int i = 0; i < list.size(); i++) {
			System.out.println(baseUrl + list.get(i).getAttribute("href"));
		}

	}
	
	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://www.iagcareers.com.au/jobs/IAG-1366005I";
		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(TIME_10S);
		System.out.println("Title: "
				+ page.getBody().getOneHtmlElementByAttribute("h1", "class", "col-xs-12 theme-heading").asText());
		System.out.println(
				"Date: " + page.getBody().getOneHtmlElementByAttribute("div", "class", "postedDate col-sm-4").asText());
		System.out.println(
				"ID: " + page.getBody().getOneHtmlElementByAttribute("div", "class", "jobReq col-sm-4").asText());
		System.out.println(
				"Location: " + page.getBody().getOneHtmlElementByAttribute("div", "class", "jobLoc col-sm-4").asText());
		String categ = page.getBody().getElementsByTagName("tbody").get(0).getElementsByTagName("tr").get(0).asText();
		System.out.println("Cate: " + categ.replace("Category:", "").trim());

		String type = page.getBody().getElementsByTagName("tbody").get(0).getElementsByTagName("tr").get(1).asText();
		System.out.println("Type: " + type.replace("Position Type:", "").trim());
		String apply = page.getBody().getOneHtmlElementByAttribute("input", "class", "rasp_button apply")
				.getAttribute("onclick");
		System.out.println("Apply: " + baseUrl + apply.replace("location.href='", "").trim());
		System.out.println("Des: " + page.getBody().getOneHtmlElementByAttribute("div", "class", "jobDesc").asText());
	}
}