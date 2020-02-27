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
 * Test NuComGroup jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-14
 */

public class TestNuComGroupHtmlUnit extends TestAbstractScrapper{

	private static final String SITE = "https://www.prosiebensat1-jobs.com/stellenangebote.html?reset_search=0&search_mode=job_filter_advanced&filter%5Bvolltext%5D=&filter%5Bclient_id%5D%5B%5D=92";
	private static WebClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}
	
	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='middle']/a");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='middle']/a");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setUrl(htmlElement.getAttribute("href"));
			System.out.println(job.getUrl());
			job.setTitle(htmlElement.getTextContent());
			System.out.println(job.getTitle());
			job.setName(job.getTitle());
			jobList.add(job);
		}
		System.out.println(jobList.size());
	}
	
	@Test
	public void testJobDetail() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='middle']/a");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setUrl(htmlElement.getAttribute("href"));
			job.setTitle(htmlElement.getTextContent());
			job.setName(job.getTitle());
			jobList.add(job);
		}
		
		for (Job job : jobList) {
			page = client.getPage(job.getUrl());
			HtmlElement category = (HtmlElement) page.getByXPath("//div[@class='emp_box_content']").get(0);
			System.out.println(category.asText());
			HtmlElement location = (HtmlElement) page.getByXPath("//div[@class='emp_box_content']").get(1);
			System.out.println(location.asText());
			HtmlElement spec = page.getFirstByXPath("//div[@class='emp_nr_left']");
			System.out.println(spec.asText());
		}
	}

}
