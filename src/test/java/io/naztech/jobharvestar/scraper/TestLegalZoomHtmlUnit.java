package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

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
 * Test LegalZoom jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-13
 */
public class TestLegalZoomHtmlUnit extends TestAbstractScrapper{

	private static final String SITE = "https://www.legalzoom.com/careers/all-positions?ccc=Search%20All%20Jobs";
	private static final String HEAD = "https://jobs.jobvite.com";
	private static WebClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(25000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//table[@class='jv-job-list']/tbody/tr/td/a");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(25000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//table[@class='jv-job-list']/tbody/tr/td/a");
		for (HtmlElement htmlElement : jobLinksE) {
			System.out.println(htmlElement.getAttribute("href"));
		}
	}
	
	@Test
	public void testJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(25000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//table[@class='jv-job-list']/tbody/tr/td/a");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setUrl(HEAD+htmlElement.getAttribute("href"));
			job.setTitle(htmlElement.getTextContent());
			jobList.add(job);
		}
		
		for (Job job : jobList) {
			page = client.getPage(job.getUrl());
			client.waitForBackgroundJavaScript(20000);
			frames = page.getFrames();
			page = (HtmlPage) frames.get(0).getEnclosedPage();
			HtmlElement applicationUrl = page.getFirstByXPath("//div[@class='jv-job-detail-top-actions']/a");
			job.setApplicationUrl(applicationUrl.getAttribute("href"));
			System.out.println(job.getApplicationUrl());
			HtmlElement spec = (HtmlElement) page.getByXPath("//div[@class='jv-wrapper']/div").get(1);
			System.out.println(spec.asText());
			
			
		}
	}

}
