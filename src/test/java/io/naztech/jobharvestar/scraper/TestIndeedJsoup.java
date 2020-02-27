/**
 * 
 */
package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tanbirul.hashan
 *
 */
@Slf4j
public class TestIndeedJsoup {
	
	private static final String JOB_LOC_PATH = "div.jobsearch-InlineCompanyRating > div";
	private static final String APPLY_PATH = "div.icl-u-lg-hide > a";
	@Test
	public void testDates() throws IOException {
		Document doc=Jsoup.connect("https://ca.indeed.com/Igm-Financial-jobs").get();
		Iterator<Element> postedDates= doc.select(getDatespath()).iterator();
		while(postedDates.hasNext()) System.out.println(postedDates.next().text());	
		
	}
	@Test
	public void testFirstPage() throws IOException {
		Document doc=Jsoup.connect("https://ca.indeed.com/Igm-Financial-jobs").get();
		Elements elements=doc.select(getJobsPath());
		Iterator<Element> postedDates= doc.select(getDatespath()).iterator();
		for (Element element : elements) {
			Job job= new Job(element.attr("href"));
			job.setTitle(element.text());
			job.setName(job.getTitle());
			System.out.println(postedDates.next().text());
			//if (job.getPostedDate() == null) log.info(" failed to parse date value " + postedDates.next() + " for job " + job.getUrl());
			System.out.println(job.toString());
		}		 		
	}
	
	@Test
	public void testNextPage() throws IOException {
		Document doc=Jsoup.connect("https://www.indeed.com/cmp/Fidelity-National-Financial/jobs").get();
		Element nextAnchorEl = doc.selectFirst("div.cmp-Pagination > a:contains(Next)");
		if(nextAnchorEl!=null) System.out.println(nextAnchorEl.attr("href"));
		else System.out.println("Page Ended");
	}
	@Test
	public void testJobDetails() throws IOException {
		Document doc=Jsoup.connect("https://www.indeed.com/viewjob?jk=0ed191d5bd2446be&fccid=c0894fc9859e9ebd&vjs=3").get();
		Job job=new Job();
		try {
			Elements elements = doc.select(JOB_LOC_PATH);
			if (elements.size() > 0) job.setLocation(elements.get(elements.size() - 1).text());
			if (!doc.select(APPLY_PATH).isEmpty()) job.setApplicationUrl(doc.select(APPLY_PATH).attr("href"));
			job.setSpec(doc.select("div.jobsearch-JobComponent-description").text());
			System.out.println(job.toString());
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			log.warn("Failed to parse job details of " + job.getUrl() + " :" + e);
		}
	}
	
	
	private String getDatespath() {
		return "span.date";
	}
	
	protected String getJobsPath() {
		return "h2.jobtitle > a";
	}

}
