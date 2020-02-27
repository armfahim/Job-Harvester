package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;

/**
 * Test CrowdStrike jobs site parsing using htmlunit.
 *  
 * @author Rahat Ahmad
 * @since 2019-03-13
 */
public class TestCrowdStrikeHtmlUnit extends TestAbstractScrapper{

	private static final String SITE = "https://www.crowdstrike.com/careers/";
	private static final String HEAD = "https://jobs.jobvite.com";
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
		List<HtmlElement> jobLinksE = page.getByXPath("//article[@class='jv-page-body']/div/table/tbody/tr");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//article[@class='jv-page-body']/div/table/tbody/tr");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			System.out.println(job.getTitle());
			job.setUrl(HEAD+htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println(job.getUrl());
			jobList.add(job);
		}
		System.out.println(jobList.size());
	}
	
	@Test
	public void testJobDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = client.getPage(SITE);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<HtmlElement> jobLinksE = page.getByXPath("//article[@class='jv-page-body']/div/table/tbody/tr");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			String jobUrl = HEAD+htmlElement.getElementsByTagName("a").get(0).getAttribute("href");
			if(jobUrl.contains("/search?")) {
				List<Job> newJobList = getJobUrl(jobUrl);
				for (int i = 0;i<newJobList.size();i++) {
					if(newJobList.get(i).getUrl().equals(jobList.get(jobList.size()-1).getUrl())) {
						for(int j = i+1 ; j < newJobList.size();j++) {
							jobList.add(newJobList.get(j));
						}
						break;
					}
				}
				continue;
			}
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			job.setUrl(HEAD+htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println(job.getUrl());
			jobList.add(job);
		}
		System.out.println(jobList.size());
		for (Job job : jobList) {
			page = client.getPage(job.getUrl());
			HtmlElement applicationUrl = page.getFirstByXPath("//div[@class='jv-job-detail-top-actions']/a");
			job.setApplicationUrl(applicationUrl.getAttribute("href"));
			System.out.println(job.getApplicationUrl());
			Document document = Jsoup.connect(job.getUrl()).get();
			job.setSpec(document.select("div.jv-wrapper > div").get(1).text());
			System.out.println(job.getSpec());
			
		}
	}
	
	private List<Job> getJobUrl(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		HtmlPage newPage = client.getPage(url);
		List<HtmlElement> jobsUrlE = newPage.getByXPath("//article[@class='jv-page-body']/div/table/tbody/tr");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobsUrlE) {
			Job job = new Job();
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			job.setName(job.getTitle());
			job.setUrl(HEAD+htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			jobList.add(job);
		}
		return jobList;
	}
	
	

}
