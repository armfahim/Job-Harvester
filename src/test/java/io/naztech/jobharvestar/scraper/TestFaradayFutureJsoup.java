package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import io.naztech.talent.model.Job;

/**
 * https://www.indeed.com/q-Faraday-Future-jobs.html
 * 
 * @author Marjana Akter
 * @since 2019-03-18
 */
public class TestFaradayFutureJsoup {

	int initial_page = 1;

	public void GetJobDetails(Job j) {

		Document document;
		try {
			document = Jsoup.connect(j.getUrl()).get();
			System.out.println(document.text());

			try {
				j.setSpec(
						document.select("div[class=jobsearch-JobComponent-description icl-u-xs-mt--md]").get(0).text());
			} catch (IndexOutOfBoundsException e) {
			}

			try {
				String jUrl = document.select("div[class=icl-u-lg-block icl-u-xs-hide icl-u-lg-textCenter]>a").get(0)
						.attr("href");
				j.setApplicationUrl(jUrl);

			} catch (IndexOutOfBoundsException e) {
				// e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("IO ex");
		}
		System.out.println(j.toString());
	}

	@Test
	public void getSummaryPages() {

		// testGetJobList1("https://www.indeed.com/jobs?q=Faraday+Future&start=0");
		Document doc = null;
		try {
			doc = Jsoup.connect("https://www.indeed.com/jobs?q=Faraday+Future&start=0").get();
			Elements elements = doc.select("div[class^=jobsearch-SerpJobCard]");
			for (Element element : elements) {
				getJobList(element);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void getJobList(Element el) {
		Job job = new Job();
		job.setTitle(el.select("h2[class=jobtitle]").get(0).text());
		job.setName(job.getName());
		job.setLocation(el.select("span[class=location]").get(0).text());
		job.setSpec(el.select("span[class=summary]").get(0).text());

		try {
			job.setUrl("https://www.indeed.com/viewjob?jk="
					+ el.select("h2[class=jobtitle]>a").get(0).attr("href").toString().split("jk=")[1].toString()
							.split("&fccid=")[0]);

			GetJobDetail(job.getUrl(), job);
		} catch (IndexOutOfBoundsException e) {
		}
	}

	public void GetJobDetail(String url, Job job) {

		System.out.println(url);

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			Document cp = Jsoup.connect(url).get();
			job.setSpec(cp.select("div[class^=jobsearch-JobComponent-description]").get(0).text());
			job.setApplicationUrl(
					cp.select("#viewJobButtonLinkContainer > div.icl-u-lg-block.icl-u-xs-hide.icl-u-lg-textCenter > a")
							.get(0).attr("href"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(job);
	}

	public String testGetNextPage(Document currentPage) {

		Document doc = null;
		int current_page = Integer.parseInt(currentPage.select("#resultsCol > div.pagination > b").get(0).text());
		System.out.println(current_page);
		String nextUrl = "https://www.indeed.com/jobs?q=Faraday+Future&start=" + Integer.toString((current_page) * 10);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>" + nextUrl);
		return nextUrl;
	}

	@Test
	public void testGetJobDetails() {
		Document d = null;
		try {
			d = Jsoup.connect("https://www.indeed.com/viewjob?jk=6c8aeb7b7d1abeb4&from=serp&vjs=3").get();
			Elements job_Title = d.select("h3[class=icl-u-xs-mb--xs icl-u-xs-mt--none jobsearch-JobInfoHeader-title]");
			Elements job_Description = d.select("div[class=jobsearch-JobComponent-description icl-u-xs-mt--md]");
			Elements company_Rating = d.select(
					"div[class=jobsearch-InlineCompanyRating icl-u-xs-mt--xs  jobsearch-DesktopStickyContainer-companyrating]");
			Elements job_Apply = d.select("div[class=icl-u-lg-block icl-u-xs-hide icl-u-lg-textCenter]>a");
			System.out.println("JOB TITLE : " + job_Title.text());
			System.out.println("JOB RATING : " + company_Rating.text());
			System.out.println("JOB DESCRIPTION  : " + job_Description.text());
			System.out.println("APPLY JOB : " + job_Apply.attr("href"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
