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
 * URL: https://www.adaptivebiotech.com/about-us/careers/listings/
 * 
 * @author masum.billa
 * @since 07.05.19
 * 
 **/
public class TestAdaptiveBiotechHtmlUnit extends TestAbstractScrapper {
	private static final String SITE = "https://www.adaptivebiotech.com/about-us/careers/listings/";
	private static WebClient client = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void testGetJobList()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScript(8000);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='opening']");
		System.out.println(jobLinksE.size());
	}

	@Test
	public void testFirstPage()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='opening']");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			System.out.println(job.getTitle());
			job.setLocation(htmlElement.getElementsByTagName("span").get(0).asText());
			System.out.println(job.getLocation());
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println(job.getUrl());
			jobList.add(job);
		}
		System.out.println(jobList.size());
	}

	@Test
	public void testJobDetail()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='opening']");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			job.setName(job.getTitle());
			job.setLocation(htmlElement.getElementsByTagName("span").get(0).asText());
			jobList.add(job);
			System.out.println(job);
		}

		for (Job job : jobList) {
			page = client.getPage(job.getUrl());
			frames = page.getFrames();
			page = (HtmlPage) frames.get(0).getEnclosedPage();
			System.out.println(page.getElementById("content").getTextContent()); // spec
		}
	}
}
