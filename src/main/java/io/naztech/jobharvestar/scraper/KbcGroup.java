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
 * KBC Group jobs site parser<br>
 * URL: https://www.kbc.ie/careers-at-kbc/careers-current-vacancies
 * 
 * @author armaan.choudhury
 * @author tanmoy.tushar
 * @since 2019-01-23
 */
@Service
@Slf4j
public class KbcGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.KBC_GROUPE;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.KBC_GROUPE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 18);
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements rowList = doc.select("a[class=button button--alt1-primary ga-cta]");
		for (Element row: rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPage(getBaseUrl() + row.attr("href") , siteMeta);			
		}
	}	

	private void getSummaryPage(String url, SiteMetaData siteMeta) throws PageScrapingInterruptedException {
		try {
			Document doc1 = Jsoup.connect(url).get();
			Elements rowList = doc1.select("div[class=content-wrapper]>p>a");
			expectedJobCount += rowList.size();
			for(Element row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				if(row.attr("href").startsWith("http")) continue;
				Job job = new Job(getBaseUrl() + row.attr("href"));
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse summary page of " + getSiteName(), e);
		}
	}

	private Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element title = doc.selectFirst("h1");
			job.setTitle(title.text().trim());
			job.setName(job.getTitle());
			Element jobE = doc.selectFirst("div[class=content-wrapper]");
			job.setSpec(jobE.text().trim());
			String[] parts = jobE.text().split(":");			
			if(parts[2].contains("Contract") && parts[3].contains("Location")) {
				job.setType(parts[3].split("Location")[0].trim());
				if(parts[4].contains("Department")) job.setLocation(parts[4].split("Department")[0].trim());
			}
			if(parts[2].contains("Contract") && parts[3].contains("Department")) {
				job.setType(parts[3].split("Department")[0].trim());
				if(parts[4].contains("Location")) job.setCategory(parts[4].split("Location")[0].trim());
			}			
			if(parts[1].contains("Contract") && parts[2].contains("Department")) {
				job.setType(parts[2].split("Department")[0].trim());
				if(parts[3].contains("Location")) job.setCategory(parts[3].split("Location")[0].trim());
			}
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
			return null;
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
