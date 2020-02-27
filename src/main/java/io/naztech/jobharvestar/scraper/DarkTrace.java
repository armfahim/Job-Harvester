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
 * DarkTrace job site parsing class. <br>
 * URL: https://www.darktrace.com/en/careers/
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-10
 */
@Slf4j
@Service
public class DarkTrace extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DARKTRACE;
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
		this.baseUrl = siteMeta.getUrl().substring(0, 25);
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		
		Job job = new Job();
		List<HtmlElement> list = page.getBody().getByXPath("//div[@id='current-opportunities']/div");
		expectedJobCount = list.size();
		for(int i=0; i<list.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			
			job.setTitle(list.get(i).getElementsByTagName("div").get(0).getElementsByTagName("h3").get(0).asText());
			job.setName(job.getTitle());
			job.setLocation(list.get(i).getElementsByTagName("div").get(0).getElementsByTagName("div").get(0).asText());
			
			String Title = job.getTitle();
			Title.replace(" ", "-");
			Title.replace("++", "pp");
			job.setUrl(baseUrl+"/en/careers/#"+Title.toLowerCase());
			try {
				saveJob(getJobDetail(job, page), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}	
	}

	private Job getJobDetail(Job job, HtmlPage page) throws IOException {
		try {
			HtmlPage page2 = CLIENT.getPage(job.getUrl());
			job.setSpec(page2.getBody().getOneHtmlElementByAttribute("div", "class", "grid-2-1").asText());
			job.setPrerequisite(page2.getBody().getOneHtmlElementByAttribute("div", "class", "grid-2-2").getElementsByTagName("div").get(0).asText());
			job.setApplyEmail(page2.getBody().getOneHtmlElementByAttribute("div", "class", "grid-2-2").getOneHtmlElementByAttribute("div", "class", "to-apply").getElementsByTagName("p").get(0).getElementsByTagName("a").get(0).asText());
			return job;
		} catch (ElementNotFoundException |ArrayIndexOutOfBoundsException e) {
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
