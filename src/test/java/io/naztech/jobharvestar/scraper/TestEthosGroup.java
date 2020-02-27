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
 * Test Ethos Group jobs site parsing using jsoup.
 *  
 * @author Rahat Ahmad
 * @since 2019-04-02
 */
public class TestEthosGroup extends TestAbstractScrapper{

	private static String URL = "http://www.ethosgroup.com/careers/";
	private static Document document;
	private static String HEAD = "https://ethosgroup.secure.force.com";
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
		Elements jobEl= document.select("a.btn");
		for(int i =3;i<jobEl.size();i++) {
			System.out.println(jobEl.get(i).attr("href"));
		}
		System.out.println(jobEl.size());
	}
	
	@Test
	public void getFirstPage() throws IOException {
		Elements jobEl= document.select("a.btn");
		List<Job> jobList = new ArrayList<>();
		for(int i =3;i<jobEl.size();i++) {
			document = Jsoup.connect(jobEl.get(i).attr("href")).get();
			Elements jobListE= document.select("table.atsSearchResultsTable > tbody > tr");
			for (int j = 0; j < jobListE.size(); j++) {
				Job job = new Job();
				job.setUrl(HEAD+jobListE.get(j).select("a").attr("href"));
				System.out.println(job.getUrl());
				job.setTitle(jobListE.get(j).select("a").text());
				System.out.println(job.getTitle());
				job.setCategory(jobListE.get(j).select("td").get(1).select("span").text());
				System.out.println(job.getCategory());
				job.setLocation(jobListE.get(j).select("td").get(2).select("span").text());
				System.out.println(job.getLocation());
				jobList.add(job);
			}
			
		}
	}
	
	@Test
	public void getJobDetailsPage() throws IOException {
		Elements jobEl= document.select("a.btn");
		List<Job> jobList = new ArrayList<>();
		for(int i =3;i<jobEl.size();i++) {
			document = Jsoup.connect(jobEl.get(i).attr("href")).get();
			Elements jobListE= document.select("table.atsSearchResultsTable > tbody > tr");
			for (int j = 0; j < jobListE.size(); j++) {
				Job job = new Job();
				job.setUrl(HEAD+jobListE.get(j).select("a").attr("href"));
				job.setTitle(jobListE.get(j).select("a").text());
				job.setCategory(jobListE.get(j).select("td").get(1).select("span").text());
				job.setLocation(jobListE.get(j).select("td").get(2).select("span").text());
				jobList.add(job);
			}
		}
		
		for (Job job : jobList) {
			document = Jsoup.connect(job.getUrl()).get();
			System.out.println(job.getUrl());
			job.setSpec(document.select("td.atsJobDetailsTdTwoColumn").text());
			System.out.println(job.getSpec());
		}
	}
	
	

}
