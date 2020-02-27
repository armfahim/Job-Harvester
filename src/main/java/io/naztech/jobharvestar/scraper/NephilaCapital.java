package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Nephila Capital job site parsing class. <br>
 * URL: http://nephila.mytribehr.com/careers/view/46
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-07
 */
@Service
@Slf4j
public class NephilaCapital extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NEPHILA_CAPITAL;
	private static WebClient CLIENT = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		CLIENT = getFirefoxClient();
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		expectedJobCount = siteMeta.getExpectedJobCount();
		Job job = new Job(page.getBaseURI());
		try {
			saveJob(getJobDetail(job, page), siteMeta);			
		} catch (Exception e) {
			exception = e;
		}
	}

	private Job getJobDetail(Job job, HtmlPage page) throws IOException {
		try {
			job.setTitle(page.getBody().getOneHtmlElementByAttribute("div", "class", "title").getElementsByTagName("div").get(0).getElementsByTagName("h2").get(0).asText().trim());
			job.setName(job.getTitle());
			job.setCategory(page.getBody().getOneHtmlElementByAttribute("div", "class", "title").getElementsByTagName("div").get(0).getElementsByTagName("h3").get(0).asText().trim());
			job.setLocation(page.getBody().getOneHtmlElementByAttribute("div", "class", "posting attributes").getElementsByTagName("dl").get(0).getElementsByTagName("dd").get(2).asText());
			job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "posting attributes").getElementsByTagName("dl").get(0).getElementsByTagName("dd").get(2).asText());
			job.setApplicationUrl(page.getBody().getOneHtmlElementByAttribute("div", "class", "applyButton").getElementsByTagName("a").get(0).getAttribute("href"));
			return job;
		} catch(ElementNotFoundException e) {
			log.warn("Failed parse job details " + job.getUrl(), e);
			return null;
		}
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
