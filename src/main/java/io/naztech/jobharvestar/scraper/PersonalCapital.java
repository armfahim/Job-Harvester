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
 * Personal Capital jobs site parse
 * Url: https://personalcapital.applytojob.com
 * 
 * @author Kowshik Saha
 * @since 2019-04-04
 */
@Slf4j
@Service

public class PersonalCapital extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PERSONAL_CAPITAL;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc = null;
		try {
			doc = Jsoup.connect(site.getUrl()).get();
			Elements e = doc.select("h4[class=list-group-item-heading]");
			expectedJobCount = e.size();
			for (Element element : e) 
			{
				if(isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(element, site), site);					
				} catch (Exception e2) {
					exception = e2;
				}
			}
		} catch (IOException e) {
			log.warn("Failed parse job details of"+ SITE, e);
			throw e;
		}
	}

	private Job getJobDetails(Element e, SiteMetaData site) {
		String url = e.select("a").first().attr("href");
		Job j = new Job();
		j.setUrl(url);
		try {
			Document doc = Jsoup.connect(url).get();
			Element jobSec = doc.select("body").first();
			j.setTitle(e.text());
			j.setName(j.getTitle());
			j.setApplicationUrl(j.getUrl());
			j.setLocation(jobSec.select("li[title=Location]").first().text());
			j.setSpec(jobSec.select("div[class=description]").first().text());
			return j;
		}

		catch (Exception iox) {
			log.warn("Failed parse job details of"+j.getUrl(), iox);
		}
		return null;
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
