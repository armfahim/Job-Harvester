package io.naztech.jobharvestar.scraper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;

/**
 * Test Coalition jobs site parsing using htmlUnit.
 *  
 * @author Rahat Ahmad
 * @since 2019-04-02
 */
public class TestCoalition extends TestAbstractScrapper{

	private static String URL = "https://www.thecoalition.com/careers#positions";
	private static WebClient client = null;
	private static String HEAD = "https://careers.jobscore.com";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void getJobList() throws IOException {
		HtmlPage page = client.getPage(URL);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobListE = page.getByXPath("//tr[@class='job clickable']");
		System.out.println(jobListE.size());
	}
	
	@Test
	public void getFirstPage() throws IOException {
		HtmlPage page = client.getPage(URL);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobListE = page.getByXPath("//tr[@class='job clickable']");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobListE) {
			Job job = new Job();
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			System.out.println(job.getTitle());
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println(job.getUrl());
			job.setCategory(htmlElement.getElementsByTagName("td").get(1).asText());
			System.out.println(job.getCategory());
			jobList.add(job);
		}
	}
	
	@Test
	public void getJobDetailsPage() throws IOException {
		HtmlPage page = client.getPage(URL);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobListE = page.getByXPath("//tr[@class='job clickable']");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobListE) {
			Job job = new Job();
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			job.setName(job.getTitle());
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			job.setCategory(htmlElement.getElementsByTagName("td").get(1).asText());
			jobList.add(job);
		}
		
		for (Job job : jobList) {
			page = client.getPage(job.getUrl());
			String[] ab = page.getBody().getOneHtmlElementByAttribute("h2", "class", "js-subtitle").getTextContent().split(Pattern.quote("|"));
			job.setLocation(ab[1]);
			job.setType(ab[2]);
			job.setSpec(page.getElementById("js-job-description").getTextContent());
			System.out.println(job.getSpec());
			HtmlElement applicationUrl = (HtmlElement) page.getByXPath("//a[@class='js-btn js-btn-block js-btn-apply']").get(0);
			job.setApplicationUrl(applicationUrl.getAttribute("href"));
			System.out.println(HEAD+job.getApplicationUrl());
			
		}
	}
	
	

}
