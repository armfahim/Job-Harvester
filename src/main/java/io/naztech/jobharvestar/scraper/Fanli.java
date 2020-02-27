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
 * Fanli job site parsing class. <br>
 * URL: https://help.fanli.com/a/about/joinus.html
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-12
 */
@Slf4j
@Service
public class Fanli extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.FANLI;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		CLIENT = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		getSummaryPages(page, siteMeta);
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) {
		try {
			List<HtmlElement> list = page.getBody().getElementsByAttribute("ul", "class", "clearfix");
			for(int i=0; i<list.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				List<HtmlElement> listE = list.get(i).getElementsByTagName("li");
				expectedJobCount += listE.size();
				for(int j=0; j<listE.size(); j++) {
					if (isStopped()) throw new PageScrapingInterruptedException();
					Job job = new Job(baseUrl+"/a/about/joinus.html"+listE.get(j).getElementsByTagName("a").get(0).getAttribute("href"));
					job.setTitle(listE.get(j).getElementsByTagName("a").get(0).asText());
					job.setName(job.getName());
					try {
					saveJob(getJobDetail(job, page), siteMeta);
					}catch(Exception e) {
						exception = e;
					}
				}
			}
		} catch (ElementNotFoundException | PageScrapingInterruptedException e) {
			log.warn("Failed parse job summary", e);
		}
	}
	private Job getJobDetail(Job job, HtmlPage page) {
		try {
			String className = job.getUrl().replace("https://help.fanli.com/a/about/joinus.html#", "").trim();
			job.setSpec(page.getElementById(className).asText());
			return job;
		} catch (ElementNotFoundException e) {
			log.warn("Failed parse job details " + job.getUrl(), e);
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
