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
 * URL: https://www.afiniti.com/careers
 * 
 * @author masum.billa
 * @since 07.05.19
 * 
 **/
public class TestAfinitiHtmlUnit  extends TestAbstractScrapper{

	private static final String SITE = "https://www.afiniti.com/careers";
	
	private static WebClient client = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='gnewtonCareerGroupRowClass']");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='gnewtonCareerGroupRowClass']");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			System.out.println(job.getTitle());
			job.setLocation(htmlElement.getElementsByTagName("div").get(1).asText());
			System.out.println(job.getLocation());
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println(job.getUrl());
			jobList.add(job);
		}
		System.out.println(jobList.size());
	}
	
	
	@Test
	public void testJobDetail() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='gnewtonCareerGroupRowClass']");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			job.setName(job.getTitle());
			job.setLocation(htmlElement.getElementsByTagName("div").get(1).asText());
			jobList.add(job);
			//System.out.println(job);
		}
		System.out.println(jobList.size());
		for (Job job : jobList) {
			page = client.getPage(job.getUrl());
			frames = page.getFrames();
			page = (HtmlPage) frames.get(0).getEnclosedPage();
			System.out.println(page.getElementById("gnewtonJobPosition").getTextContent());
			System.out.println(page.getElementById("gnewtonJobLocationInfo").getTextContent());
			System.out.println(page.getElementById("gnewtonJobID").getTextContent());
			System.out.println(page.getElementById("gnewtonJobDescriptionText").getTextContent()); 
		}
	}

}
