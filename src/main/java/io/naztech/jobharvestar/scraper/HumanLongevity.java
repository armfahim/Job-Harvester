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
 * Human Longevity Jobsite Parser <br>
 * URL : https://humanlongevityinc.applytojob.com/apply
 * 
 * @author Armaan Seraj Choudhury
 * @author iftekar.alam
 * @since 2019-03-12
 */
@Service
@Slf4j
public class HumanLongevity extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.HUMAN_LONGEVITY;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements rowList = doc.select("ul[class=list-group]>li>h4>a");
		expectedJobCount=rowList.size();
		for (Element el : rowList) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(el.attr("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+job.getUrl());
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=description]").text().trim());
		Element jobE = doc.selectFirst("ul[class=list-inline job-attributes]>li");
		if(jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.select("ul[class=list-inline job-attributes]>li").get(1);
		if(jobE != null)  job.setType(jobE.text().trim());
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
