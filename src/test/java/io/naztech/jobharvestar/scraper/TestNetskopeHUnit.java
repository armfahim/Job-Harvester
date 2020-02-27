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
import lombok.extern.slf4j.Slf4j;
/**
 * URL: https://www.netskope.com/company/careers/open-positions
 * 
 * @author masum.billa
 * @since 12.05.19
 * 
 **/
@Slf4j
public class TestNetskopeHUnit  extends TestAbstractScrapper{

	private static final String SITE = "https://www.netskope.com/company/careers/open-positions";
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
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='opening']");
		System.out.println(jobLinksE.size());
	}
	
	@Test
	public void testFirstPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
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
	public void jobDetailsTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String url = "https://www.netskope.com/company/careers/open-positions?gh_jid=1598816";

		HtmlPage page = client.getPage(url);
		client.waitForBackgroundJavaScript(TIME_1M);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		HtmlElement title = page.getFirstByXPath("//h1[@class='app-title']");
		System.out.println(title.asText());
		HtmlElement location = page.getFirstByXPath("//div[@class='location']");
		System.out.println(location.asText());
		HtmlElement body = page.getFirstByXPath("//div[@id='content']");
		System.out.println(body.asText());
	}


}
