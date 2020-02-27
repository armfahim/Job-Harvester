package io.naztech.jobharvestar.scraper;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;

/**
 * Test LookOut jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-14
 */
public class TestLookOutHtmlUnit extends TestAbstractScrapper{

	private static final String SITE = "https://www.lookout.com/about/careers/jobs";
	private static WebClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
//		client.waitForBackgroundJavaScript(15000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@id='wrapper']/section/div");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@id='wrapper']/section/div");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println(job.getUrl());
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			System.out.println(job.getTitle());
			job.setLocation(htmlElement.getElementsByTagName("span").get(0).asText());
			System.out.println(job.getLocation());
			jobList.add(job);
		}
		System.out.println(jobList.size());
	}
	
	@Test
	public void testJobDetail() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@id='wrapper']/section/div");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			job.setName(job.getTitle());
			job.setLocation(htmlElement.getElementsByTagName("span").get(0).asText());
			jobList.add(job);
		}
		
		for (Job job : jobList) {
			page = client.getPage(job.getUrl());
			frames = page.getFrames();
			page = (HtmlPage) frames.get(0).getEnclosedPage();
			System.out.println(page.getElementById("content").asText());//spec
		}
	}

}
