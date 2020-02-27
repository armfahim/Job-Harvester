package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestSeilnvestmentsHtmlUnit extends TestAbstractScrapper {
	private static WebClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void testGetScrapedJobs() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage("https://careers.seic.com/global/en/global-job-opportunities");
		client.waitForBackgroundJavaScript(TIME_10S * 2);
		List<HtmlElement> paginationlList = page.getBody().getByXPath("//ul[@class='pagination au-target']/li/a");
		System.out.println(paginationlList.size());
		for (int i = 1; i < paginationlList.size(); i++) {
			System.out.println(paginationlList.get(i).getAttribute("href"));
		}
	}

	@Test
	public void testGetSummaryPages() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage("https://careers.seic.com/global/en/global-job-opportunities");
		client.waitForBackgroundJavaScript(TIME_10S * 2);
		List<HtmlElement> jobList = page.getBody().getByXPath("//div[@class='information']/a");
		System.out.println(jobList.size());

		for (int i = 0; i < jobList.size(); i++) {
			System.out.println(jobList.get(i).getAttribute("href"));
		}

	}

	@Test
	public void testGetJobDetail() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage("https://careers.seic.com/global/en/job/18009/Financial-Reporting-Supervisor");
		client.waitForBackgroundJavaScript(TIME_10S * 2);
		HtmlElement jobTitle = page.getBody().getOneHtmlElementByAttribute("h1", "class", "job-title");
		System.out.println(jobTitle.getTextContent().trim());

		HtmlElement jobId = page.getBody().getOneHtmlElementByAttribute("span", "class", "job-details-jobId au-target");
		System.out.println(jobId.getTextContent().trim());

		HtmlElement postedDate = page.getBody().getOneHtmlElementByAttribute("span", "class",
				"job-details-postedDate au-target");
		System.out.println(postedDate.getTextContent().split("posted")[1].trim());

		HtmlElement loaction = page.getBody().getOneHtmlElementByAttribute("span", "data-ph-id",
				"ph-page-element-page11-RfiS5F");
		System.out.println(loaction.getTextContent().split("location")[1].trim());

		HtmlElement jobType = page.getBody().getOneHtmlElementByAttribute("span", "data-ph-id",
				"ph-page-element-page11-268IYb");
		System.out.println(jobType.getTextContent().split("type")[1].trim());

		HtmlElement jobCategory = page.getBody().getOneHtmlElementByAttribute("span", "data-ph-id",
				"ph-page-element-page11-0BrETF");
		System.out.println(jobCategory.getTextContent().split("category")[1].trim());

		HtmlElement applyLink = page.getFirstByXPath("//a[@title='Apply Now']");
		System.out.println(applyLink.getAttribute("href"));

		HtmlElement jobDescription = page.getFirstByXPath("//section[@class='job-description']");
		System.out.println(jobDescription.getTextContent());

	}
}
