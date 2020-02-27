package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * Quanergy Systems job site parsing class. <br>
 * URL: https://quanergy.recruiterbox.com/
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-12
 */
@Slf4j
@Service
public class QuanergySystems extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.QUANERGY_SYSTEMS;
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
		this.baseUrl = siteMeta.getUrl().substring(0, 33);		
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		getSummaryPages(page, siteMeta);
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<HtmlElement> allJobLink = page.getBody().getElementsByAttribute("a", "class", "card-title");
		List<HtmlElement> allLocation= page.getBody().getElementsByAttribute("div", "class", "card-info cut-text");
		List<HtmlElement> allType= page.getBody().getElementsByAttribute("small", "class", "badge-opening-meta");
		expectedJobCount = allJobLink.size();
		for(int i=0; i<allJobLink.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();			
			Job job = new Job(baseUrl+allJobLink.get(i).getAttribute("href"));
			job.setLocation(allLocation.get(i).getAttribute("data-original-title"));
			job.setType(allType.get(i).asText());
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		try {
			HtmlPage page = CLIENT.getPage(job.getUrl());
			CLIENT.waitForBackgroundJavaScript(TIME_5S);
			job.setTitle(page.getBody().getOneHtmlElementByAttribute("h1", "class", "jobtitle meta-job-detail-title").asText());
			job.setName(job.getTitle());
			job.setApplicationUrl(page.getBody().getOneHtmlElementByAttribute("a", "class", "btn btn-primary btn-lg btn-apply hidden-print").getAttribute("href"));
			job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "jobdesciption").asText());
			return job;
		} catch (ElementNotFoundException e) {
			log.warn("Failed parse job details " + job.getUrl()+e);
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
