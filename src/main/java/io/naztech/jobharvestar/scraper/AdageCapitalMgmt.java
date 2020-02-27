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
 * Adage Capital Mgmt job site parsing class. <br>
 * URL: https://about.crunchbase.com/about-us/careers/
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-04
 */
@Slf4j
@Service
public class AdageCapitalMgmt extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ADAGE_CAPITAL_MGMT;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 28);
		CLIENT = getFirefoxClient();
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);

		List<HtmlElement> list = page.getBody().getElementsByTagName("a");
		List<String> allJobLink = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getAttribute("href").contains("jobs.lever.co"))
				allJobLink.add(list.get(i).getAttribute("href"));
		}
		expectedJobCount = allJobLink.size();
		getSummaryPages(allJobLink, siteMeta);
	}

	private void getSummaryPages(List<String> allJobLink, SiteMetaData siteMeta) throws IOException, InterruptedException{
		HtmlPage page;
		Job job = new Job();
		for (int i = 0; i < allJobLink.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			
			page = CLIENT.getPage(allJobLink.get(i));
			CLIENT.waitForBackgroundJavaScript(TIME_5S);
			
			job.setUrl(allJobLink.get(i));
			try {
				saveJob(getJobDetail(page,job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(HtmlPage page, Job job) throws IOException {
		try {
			job.setTitle(page.getBody().getOneHtmlElementByAttribute("div", "class", "posting-headline").getElementsByTagName("h2").get(0).asText().trim());
			job.setName(job.getTitle());
			job.setLocation(page.getBody().getOneHtmlElementByAttribute("div", "class", "posting-categories").getElementsByTagName("div").get(0).asText().trim());
			job.setCategory(page.getBody().getOneHtmlElementByAttribute("div", "class", "posting-categories").getElementsByTagName("div").get(1).asText().trim());
			job.setType(page.getBody().getOneHtmlElementByAttribute("div", "class", "posting-categories").getElementsByTagName("div").get(2).asText().trim());
			job.setApplicationUrl(page.getBody().getOneHtmlElementByAttribute("a", "class", "postings-btn template-btn-submit cerulean").getAttribute("href"));
			job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "section page-centered").asText().trim());
			return job;
		} catch (ElementNotFoundException e) {
			log.warn("Failed parse job details " + job.getUrl()+e);
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
		CLIENT.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
