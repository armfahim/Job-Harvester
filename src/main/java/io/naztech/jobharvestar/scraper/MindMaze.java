package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Mind Maze job site parsing class. <br>
 * URL: https://www.mindmaze.com/work-with-us/
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-12
 */
@Slf4j
@Service
public class MindMaze extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MINDMAZE;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		CLIENT = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		getSummaryPages(page, siteMeta);
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<String> allJobLink = new ArrayList<>(); 
		List<String> allJobType = new ArrayList<>();
		List<HtmlElement> listEven = page.getBody().getElementsByAttribute("tr", "class", "srJobListJobEven");
		List<HtmlElement> listOdd = page.getBody().getElementsByAttribute("tr", "class", "srJobListJobOdd");
		
		for(int i=0; i<listEven.size(); i++) {
			allJobLink.add(listEven.get(i).getAttribute("onclick"));
			allJobType.add(listEven.get(i).getElementsByTagName("td").get(1).asText());
		}
		for(int i=0; i<listOdd.size(); i++) {
			allJobLink.add(listOdd.get(i).getAttribute("onclick"));
			allJobType.add(listOdd.get(i).getElementsByTagName("td").get(1).asText());
		}
		expectedJobCount = allJobLink.size();
		for(int i=0; i<allJobLink.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();			
			Job job = new Job(allJobLink.get(i).substring(13, allJobLink.get(i).length()-3));
			job.setCategory(allJobType.get(i));
			try {
			saveJob(getJobDetail(job), siteMeta);
			}catch(Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		try {
			HtmlPage page = CLIENT.getPage(job.getUrl());
			CLIENT.waitForBackgroundJavaScript(TIME_5S);	
			job.setTitle(page.getBody().getOneHtmlElementByAttribute("h1", "class", "job-title").asText());
			job.setName(job.getTitle());
			job.setLocation(page.getBody().getOneHtmlElementByAttribute("li", "itemprop", "jobLocation").asText());
			job.setType(page.getBody().getOneHtmlElementByAttribute("li", "itemprop", "employmentType").asText());
			job.setSpec(page.getBody().getOneHtmlElementByAttribute("section", "id", "st-jobDescription").asText());
			job.setPrerequisite( page.getBody().getOneHtmlElementByAttribute("section", "id", "st-qualifications").asText());
			return job;
		} catch (ElementNotFoundException e) {
			log.warn("Failed parse job details of" + job.getUrl()+e);
			return job;
		}
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
		CLIENT.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
