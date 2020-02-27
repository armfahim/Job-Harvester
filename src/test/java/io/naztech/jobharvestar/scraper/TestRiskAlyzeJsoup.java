package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * Test RiskAlyze jobsite Parser in Jsoup<br>
 * URL: https://www.riskalyze.com/careers
 * 
 * @author jannatul.maowa
 * @since 2019-04-28
 */
public class TestRiskAlyzeJsoup extends TestAbstractScrapper {
	@Test
	public void testGetScrapedJobs() throws IOException {
		Document document = Jsoup.connect("https://www.riskalyze.com/careers").get();
		Elements jobList = document.select("h3[class=whr-title]").select("a");
		System.out.println(jobList.size());
	}

	@Test
	public void testJobDetails() throws IOException {
		Document document = Jsoup.connect("https://www.workable.com/j/F77AF57BC7").get();
		Element jobE = document.selectFirst("section[class=section section--header]").selectFirst("h1");
		System.out.println(jobE.text());
		jobE = document.selectFirst("p[class=meta]");
		System.out.println(jobE.text());
		jobE = document.selectFirst("section[class=section section--text]");
		System.out.println("JobSpec:  " + jobE.text());
		jobE = document.selectFirst("a[class=btn btn--primary btn--large apply-workable]");
		System.out.println("ApplicationLink: " + jobE.attr("href"));
	}
}
