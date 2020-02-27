package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

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
 * Oxford Nanopore <br>
 * URL : https://nanoporetech.com/about-us/careers/all
 * 
 * @author Armaan Seraj Choudhury
 * @author bm.alamin
 * @since 2019-03-13
 */
@Service
@Slf4j
public class OxfordNanopore extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.OXFORD_NANOPORE_TECHNOLOGIES;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private WebClient client;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.OXFORD_NANOPORE_TECHNOLOGIES));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		client = getFirefoxClient();
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setCssEnabled(false);
		baseUrl = siteMeta.getUrl().toString().substring(0, 24);
		getSummaryPage(siteMeta);
	}

	private void getSummaryPage(SiteMetaData siteMeta) throws InterruptedException, IOException {
		try {	
			HtmlPage page = client.getPage(siteMeta.getUrl());
			List<HtmlElement> links = page.getBody().getByXPath("//div[@class = 'loc-faq']/div/div");
			expectedJobCount = links.size();
			for (HtmlElement el : links) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl()+el.getAttribute("about"));
				job.setTitle(el.getTextContent().trim());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetail(job, page), siteMeta);	
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Exception in getSummaryPage: " + e);
			throw e;
		}
	}

	private Job getJobDetail(Job job, HtmlPage page) {
		try {
			page= client.getPage(job.getUrl());
			
			HtmlElement el = page.getBody().getFirstByXPath("//a[@class = 'button button--primary button--icon']");
			job.setApplicationUrl(el.getAttribute("href"));
			
			el = page.getBody().getFirstByXPath("//div[@class = 'search__item']/div");
			job.setSpec(el.getTextContent().trim());
			
			el = page.getBody().getFirstByXPath("//div[@class = 'search__item']/div/div/p");
			job.setLocation(el.getTextContent().split(":")[1].trim());
			
			return job;
		} catch (IOException e) {
			log.warn("Failed to parsed detail page", job.getUrl(), e);
		}
		return null;
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
