package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestCapitalFloatHTMlUni extends TestAbstractScrapper {

	String jobUrl = "https://capitalfloat.darwinbox.in/jobs";
	String baseUrl = "https://capitalfloat.darwinbox.in";

	private static WebClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.close();
	}

	@Test
	public void testJobSummaryPage() throws IOException, InterruptedException {
		try {

		//	Job job = new Job();

			HtmlPage page = client.getPage(jobUrl);
			// System.out.println(jobUrl.substring(0, 33));

			client.waitForBackgroundJavaScript(TIME_5S);

//			List<HtmlElement> jobList1 = page.getByXPath("//tr[@role='row']");
//			System.out.println("Number of Job in Page1: " + jobList1.size());
//
//			System.out.println(jobList1.get(1).getElementsByTagName("td").get(0).getElementsByTagName("a").get(0)
//					.getAttribute("href"));
//
//			/** Going to next page **/
//
//			List<HtmlElement> nextPageEl = page.getByXPath("//div[@class='dataTables_paginate paging_simple_numbers']");
//			page = nextPageEl.get(0).getElementsByTagName("a").get(3).click();
//			Thread.sleep(TIME_1S);
//			String buttonClass = nextPageEl.get(0).getElementsByTagName("a").get(3).getAttribute("class");
//			List<HtmlElement> jobList2 = page.getByXPath("//tr[@role='row']");
//			System.out.println(jobList2.size());

			List<HtmlElement> nextPageEl = page.getByXPath("//div[@class='dataTables_paginate paging_simple_numbers']");

			do {
				List<HtmlElement> jobList = page.getByXPath("//tr[@role='row']");
				System.out.println(jobList.size());
				nextPageEl = page.getByXPath("//div[@class='dataTables_paginate paging_simple_numbers']");
				page = nextPageEl.get(0).getElementsByTagName("a").get(3).click();
				Thread.sleep(TIME_1S);
				System.out.println("Inside scrapped jobs");

				if ((nextPageEl.get(0).getElementsByTagName("a").get(3).getAttribute("class"))
						.equals("paginate_button next disabled")) {
					jobList = page.getByXPath("//tr[@role='row']");
					System.out.println(jobList.size());
					break;
				}

			} while (true);

			// System.out.println(myPage.getElementsByTagName("a").get(3).getAttribute("class"));
		} catch (FailingHttpStatusCodeException e) {

			log.error("Error on testing Summary Page", e, e);
		}
	}

	@Test
	public void testJobDetailPage() throws IOException {

		try {
//			String jobLink = "https://capitalfloat.darwinbox.in/jobs/jobsdetailed/id/5c9b0a2036920";
//
//			HtmlPage htmlPage = client.getPage(jobLink);
//
//			List<HtmlElement> headerListEl = htmlPage.getByXPath("//div[@class='col-md-10']");

		//	HtmlElement headerEl = headerListEl.get(0); // xpath gives only list of one element

//			String jobTitle = headerEl.getElementsByTagName("p").get(0).asText();
//
//			List<HtmlElement> locationElList = headerEl.getByXPath("//div[@class='col-md-3']/span");

//			String jobLocation = locationElList.get(0).asText();
//			String jobCategory = locationElList.get(1).asText();
//
//			HtmlElement urlEl = (HtmlElement) htmlPage.getByXPath("//div[@class='col-md-2  text-right']/a").get(0);

		//	String applyUrl = "https://capitalfloat.darwinbox.in" + urlEl.getAttribute("href");

//			HtmlElement jobDescEl = (HtmlElement) htmlPage.getByXPath("//div[@class='currentjob-desc']").get(0);
//			System.out.println(jobDescEl.asText());

		} catch (FailingHttpStatusCodeException e) {

			log.error("Error on testing Detail Page", e, e);
		}
	}

}
