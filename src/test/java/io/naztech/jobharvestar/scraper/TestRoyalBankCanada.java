package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestRoyalBankCanada extends TestAbstractScrapper {
	private static final String SITE = "https://jobs.rbc.com/ca/en/search-results?keywords=";
	private static WebClient CLIENT = null;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void test() throws IOException {

	}

	@Test
	public void testDates() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(5 * TIME_10S);

		List<HtmlElement> dates = page.getBody().getElementsByAttribute("span", "class", "job-date");
		System.out.println("Total job Link = " + dates.size());
		for (int i = 0; i < dates.size(); i++) {
			System.out.println( dates.get(i).asText());
			System.out.println("=================================================================");
			System.out.println(parseDate(dates.get(i).asText().replace("Posted", "").trim(), DF));
		}
	}

	@Test
	public void testGetJobList() {

	}

	@Test
	public void testFirstPage() throws IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(5 * TIME_10S);

		String totalJob = page.getBody().getOneHtmlElementByAttribute("div", "class", "phs-jobs-list-count au-target").getAttribute("data-ph-at-count");
		System.out.println("Total Job = " + totalJob);

		List<HtmlElement> jobLinks = page.getBody().getElementsByAttribute("a", "ph-tevent", "job_click");
		List<HtmlElement> title = page.getBody().getElementsByAttribute("h4", "data-ph-id","ph-page-element-page15-dkUeku-dkUeku-81");
		List<HtmlElement> locations = page.getBody().getElementsByAttribute("span", "class", "job-location au-target");
		List<HtmlElement> dates = page.getBody().getElementsByAttribute("span", "class", "job-date");
		List<HtmlElement> cate = page.getBody().getElementsByAttribute("span", "class", "job-category au-target");
		System.out.println("Total job Link = " + jobLinks.size());
		for (int i = 0; i < jobLinks.size(); i++) {
			System.out.println(jobLinks.get(i).getAttribute("href"));
			System.out.println(title.get(i).asText());
			System.out.println(locations.get(i).asText() + " : " + cate.get(i).asText() + " : " + dates.get(i).asText());
			System.out.println("=================================================================");
		}
	}

	@Test
	public void testGetNextPage() throws InterruptedException, IOException {

		this.baseUrl = SITE.substring(0, 20);

		HtmlPage page = CLIENT.getPage(SITE);
		CLIENT.waitForBackgroundJavaScript(5 * TIME_10S);
		List<HtmlElement> jobLinks = page.getBody().getElementsByAttribute("a", "ph-tevent", "job_click");
		for (int i = 0; i < jobLinks.size(); i++) {
			System.out.println(jobLinks.get(i).getAttribute("href"));
			System.out.println("=================================================================");
		}
		int totalPage = getPageCount(page.getBody().getOneHtmlElementByAttribute("div", "class", "phs-jobs-list-count au-target").getAttribute("data-ph-at-count"),50);

		List<String> allPageLink = new ArrayList<>();
		System.out.println("Total Page = " + totalPage);
		System.out.println("=================================================================");
		for (int i = 1; i < totalPage; i++) {
			allPageLink.add(baseUrl + "/ca/en/search-results?keywords=&from=" + (i * 50) + "&s=1");
			System.out.println(baseUrl + "/ca/en/search-results?keywords=&from=" + (i * 50) + "&s=1");
		}
		System.out.println("=================================================================");
		page = CLIENT.getPage(allPageLink.get(0));
		CLIENT.waitForBackgroundJavaScript(5 * TIME_10S);
		jobLinks = page.getBody().getElementsByAttribute("a", "ph-tevent", "job_click");
		for (int i = 0; i < jobLinks.size(); i++) {
			System.out.println(jobLinks.get(i).getAttribute("href"));
			System.out.println("=================================================================");
		}

	}

	@Test
	public void testGetJobDetails() throws IOException {
		String Link = "https://jobs.rbc.com/ca/en/job/RBCAA008887625/Portfolio-Management-Assistant";

		HtmlPage page = CLIENT.getPage(Link);
		CLIENT.waitForBackgroundJavaScript(5 * TIME_10S);

		String des = page.getBody().getOneHtmlElementByAttribute("section", "class", "job-description").asText().trim();
		System.out.println(des);
	}
}