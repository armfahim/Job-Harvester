package io.naztech.jobharvestar.scraper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.naztech.talent.model.Job;

/**
 * Test WeLab jobs site parsing using jsoup.
 *  https://www.welab.co/en/careers
 * @author Rahat Ahmad
 * @since 2019-03-31
 */

public class TestWeLab extends TestAbstractScrapper{

	private static String URL = "https://www.welab.co/en/careers";
	private static Document document;
	private static String HEAD = "https://www.welab.co";
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
		Elements jobEl= document.select("a.button");
		for (int i = 0; i<jobEl.size();i++) {
			System.out.println(jobEl.get(i).attr("href"));
		}
	}
	
	@Test
	public void getFirstPage() throws IOException {
		Elements jobEl= document.select("a.button");
		//System.out.println(jobEl.size());
		List<Job> jobList = new ArrayList<>();
		for (int i = 1; i<jobEl.size();i++) {
			document = Jsoup.connect(HEAD+jobEl.get(i).attr("href")).get();
			Elements jobEl1= document.select("div.wrapper:has(h2.title)");
			for(int j=0;j<jobEl1.size();j++) {
				String category = jobEl1.get(j).select("h2.title").text();
				Elements jobLinksE = jobEl1.get(j).select("a");
				for (int k = 0; k < jobLinksE.size(); k++) {
					Job job = new Job();
					job.setCategory(category);
					System.out.println(job.getCategory());
					job.setUrl(jobLinksE.get(k).attr("href"));
					System.out.println(job.getUrl());
					job.setTitle(jobLinksE.get(k).select("span").text());
					System.out.println(job.getTitle());
					jobList.add(job);
				}
			}
			System.out.println(jobEl1.size());
		}
	}
	
	@Test
	public void getJobDetail() throws IOException {
		Elements jobEl= document.select("a.button");
		List<Job> jobList = new ArrayList<>();
		for (int i = 1; i<jobEl.size();i++) {
			document = Jsoup.connect(HEAD+jobEl.get(i).attr("href")).get();
			Elements jobEl1= document.select("div.wrapper:has(h2.title)");
			for(int j=0;j<jobEl1.size();j++) {
				String category = jobEl1.get(j).select("h2.title").text();
				Elements jobLinksE = jobEl1.get(j).select("a");
				for (int k = 0; k < jobLinksE.size(); k++) {
					Job job = new Job();
					job.setCategory(category);
					job.setUrl(HEAD+jobLinksE.get(k).attr("href"));
					job.setTitle(jobLinksE.get(k).select("span").text());
					jobList.add(job);
				}
			}
		}
		
		for (Job job : jobList) {
			document = Jsoup.connect(job.getUrl()).get();
			System.out.println(document.select("div.job:has(div.description)").text());
			System.out.println();
			
		}
	}

}
