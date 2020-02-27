package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Houzz jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-12
 */
public class TestHouzzJsoup extends TestAbstractScrapper{

	private static String URL = "https://www.houzz.com/jobs#career";
	private static Document document;
	private static String HEAD = "https://www.houzz.com";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(URL).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}
	
	@Test
	public void getJobList() {
		Elements jobEl= document.select("div#filter-by-dep > a");
		System.out.println(jobEl.size());
	}
	
	@Test
	public void getFirstPage() {
		Elements jobEl= document.select("div#filter-by-dep > a");
		for (int i =0;i<jobEl.size();i++) {
			System.out.println(jobEl.get(i).attr("href"));
		}
	}
	
	@Test
	public void getSummaryPage() throws IOException {
		Elements jobEl= document.select("div#filter-by-dep > a");
		List<String> jobUrl = new ArrayList<>();
		for (int i =0;i<jobEl.size();i++) {
			jobUrl.add(HEAD+jobEl.get(i).attr("href"));
		}
		List<String> jobUrls = new ArrayList<>();
		for (String string : jobUrl) {
			Document documentNew = Jsoup.connect(string).get();
			Elements jobs = documentNew.select("table.jobs-table > tbody > tr:has(td) > td:has(a) > a");
			System.out.println(jobs.size());
			for(int i = 0 ; i < jobs.size();i+=3) {
				if(jobs.get(i).attr("href").isEmpty()) i++;
				jobUrls.add(jobs.get(i).attr("href"));
			}
		}
		System.out.println(jobUrls.size());
	}
	
	@Test
	public void getJobDetails() throws IOException {
		Elements jobEl= document.select("div#filter-by-dep > a");
		List<String> jobUrl = new ArrayList<>();
		for (int i =0;i<jobEl.size();i++) {
			jobUrl.add(HEAD+jobEl.get(i).attr("href"));
		}
		List<String> urls = new ArrayList<>();
		for (String string : jobUrl) {
			Document documentNew = Jsoup.connect(string).get();
			Elements jobs = documentNew.select("table.jobs-table > tbody > tr:has(td) > td:has(a) > a");
			System.out.println(jobs.size());
			for(int i = 0 ; i < jobs.size();i+=3) {
				if(jobs.get(i).attr("href").isEmpty()) i++;
				urls.add(HEAD+jobs.get(i).attr("href"));
			}
			
			for (String url : urls) {
				Document doc = Jsoup.connect(url).get();
				System.out.println(doc.select("div.job-content > div").get(0).text());
				System.out.println(doc.select("div.job-content > div").get(1).text());
				System.out.println(doc.select("div.job-content").text());
				System.out.println(doc.select("div.job__applyButton > a").attr("href"));
				
			}
		}
	}
	
}
