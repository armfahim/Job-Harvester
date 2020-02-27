package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Ucommune job site parsing class. <br>
 * URL: https://ucommune.com.hk/career/
 * 
 * @author marjana.akter 
 * @since 2019-03-19
 */
@Slf4j
@Service
public class Ucommune extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UCOMMUNE;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		getSummaryPages(site);
	}

	private void getSummaryPages(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		try {
			Document doc = Jsoup.connect(site.getUrl()).get();
			Elements titleListE = doc.select(
					"#post-2294 > div > div:nth-child(5) > div > div > div > div.fusion-tabs.fusion-tabs-1.clean.vertical-tabs.icon-position-left > div.nav > ul > li");
			expectedJobCount = titleListE.size();
			for (Element element : titleListE) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setTitle(element.text());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetails(element, doc, job), site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse job details", e);
			throw e;
		}
	}

	private Job getJobDetails(Element el, Document doc, Job job) {
		job.setSpec(doc.getElementById(el.getElementsByTag("a").get(0).attr("href").replace("#", "")).text());
		job.setUrl(getJobHash(job));
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}