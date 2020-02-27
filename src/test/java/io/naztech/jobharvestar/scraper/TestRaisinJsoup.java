package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * Test Raisin jobs site parsing using Jsoup<br>
 * URL: https://www.raisin.com/careers/
 * 
 * @author jannatul.maowa
 * @since 2019-04-28
 */
public class TestRaisinJsoup extends TestAbstractScrapper {
	@Test
	public void testGetJobList() throws IOException {
		Document document = Jsoup.connect("https://www.raisin.com/careers/").get();
		Elements jobE = document.select("a[class=job-item]");
		System.out.println(jobE.size());
		for (int i = 0; i < jobE.size(); i++) {
			System.out.println(jobE.get(i).attr("href"));
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String jobUrl = "https://raisin-jobs.personio.de/job/96248";
		Document document = Jsoup.connect(jobUrl).get();
		Element jobE = document.selectFirst("div[class= col-sm-10 job-detail-desc]").selectFirst("h1");
		System.out.println(jobE.text());
		jobE = document.selectFirst("div[class= col-sm-10 job-detail-desc]").selectFirst("p");
		System.out.println(jobE.text().split("\u00B7")[0].trim());
		System.out.println(jobE.text().split("\u00B7")[1].trim());
		System.out.println(jobUrl + "#apply");
		jobE = document.selectFirst("div[class=inner]");
		System.out.println("jobPrerecuisit: " + jobE.text());
		jobE = document.selectFirst("div[class=col-md-6 pull-right]");
		System.out.println("Jobspec: " + jobE.text());
		jobE = document.selectFirst("div[class=col-md-6]");
		System.out.println("JobSpec: " + jobE.text());
	}
}
