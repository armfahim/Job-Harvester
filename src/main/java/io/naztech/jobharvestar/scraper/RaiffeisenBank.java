package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Raiffeisen Bank International<br>
 * URL: https://jobs.rbinternational.com/job-offers.html?start=0
 * 
 * @author Armaan Seraj Choudhury
 * @author iftekar.alam
 * @since 2019-02-12
 */
@Slf4j
@Service
public class RaiffeisenBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.RAIFFEISEN_BANK_INTL;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.RAIFFEISEN_BANK_INTL));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws ElementNotFoundException, IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements row = doc.select("table[id =joboffers]>tbody>tr");	
		expectedJobCount = row.size();
		for (int i = 0; i <= row.size() - 1; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(row.get(i).getElementsByTag("td").get(0).getElementsByTag("a").get(0).attr("href"));
			job.setTitle(row.get(i).getElementsByTag("td").get(0).getElementsByTag("a").get(0).text());
			job.setName(job.getTitle());
			job.setCategory(row.get(i).getElementsByTag("td").get(2).text().trim());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn(" Failed parse job details of " + job.getUrl(), e);
			}
		}
	}	

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element spce = doc.selectFirst("div[id=emp_nr_innerframe]");
		if(spce == null) spce = doc.selectFirst("div[class=emp_nr_innerframe]");
		job.setSpec(spce.text().trim());
		Element dec = doc.selectFirst("div[id=btn_online_application]>a");
		if(dec==null) dec = doc.selectFirst("div[class=btn_online_application]>a");
		job.setApplicationUrl(dec.attr("href"));
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}