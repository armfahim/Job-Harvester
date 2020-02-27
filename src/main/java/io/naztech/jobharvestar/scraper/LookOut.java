package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * LookOut Job site Parser<br>
 * URL: https://www.lookout.com/about/careers/jobs
 * 
 * @author rahat.ahmad
 * @since 2019-03-14
 */
@Slf4j
@Service
public class LookOut extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.LOOKOUT;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(siteMeta.getUrl());
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<Job> jobList = getSummaryPage(page);
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			try {
				saveJob(getJobDetails(job, page), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}

	}

	private List<Job> getSummaryPage(HtmlPage page) throws PageScrapingInterruptedException {
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@id='wrapper']/section/div");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setUrl(htmlElement.getElementsByTagName("a").get(0).getAttribute("href"));
			job.setTitle(htmlElement.getElementsByTagName("a").get(0).asText());
			job.setName(job.getTitle());
			job.setLocation(htmlElement.getElementsByTagName("span").get(0).asText());
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job, HtmlPage page)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		page = client.getPage(job.getUrl());
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		try {
			job.setSpec(page.getElementById("content").asText());
		} catch (ElementNotFoundException e) {
			log.warn(" failed to parse detail page of" + job.getUrl(), e);
		}
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}
	
	@Override
	protected void destroy() {
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
