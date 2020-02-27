package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

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
 * Touchbistro job site parsed <br>
 * URL: https://www.touchbistro.com/careers/
 * 
 * @author masum.billa
 * @author iftekar.alam
 * @since 2019-04-02
 */
@Slf4j
@Service
public class Touchbistro  extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TOUCHBISTRO;
	private static WebClient webClient;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));	
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getChromeClient();
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S);
		List<HtmlElement> jobUrl = page.getByXPath("//li[@class='single-job-item']/a");
		expectedJobCount = jobUrl.size();
		for (HtmlElement el : jobUrl) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+ job.getUrl(),e);
			}
		}
	}
		
	private Job getJobDetails(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.selectFirst("div[class=job-header]>div>h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=description]").text().trim());
		Element jobE=doc.selectFirst("li[title=Location]");
		if(jobE != null) job.setLocation(jobE.text().trim());
		jobE=doc.selectFirst("li[id=resumator-job-employment]");
		if(jobE != null) job.setType(jobE.text().trim());
		jobE=doc.selectFirst("li[title=Department]");
		if(jobE != null) job.setCategory(jobE.text().trim());
		return job;
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
