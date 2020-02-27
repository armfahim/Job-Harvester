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
 * NTC<br> 
 * URL: https://northerntrustcareers.com/jobs/search/8498650
 * 
 * @author Md. Sanowar Ali
 * @author iftekar.alam
 * @since 2019-02-27
 */
@Service
@Slf4j
public class NorthernTrust extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NORTHERN_TRUST_CORP;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		int totalPage = getTotalPage(site.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/page" + i;
			try {
				browseJobList(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}			
		}
	}
	
	private void browseJobList(String url, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.select("div.jlr_title");
		for (Element element : els) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(element.child(1).child(0).attr("href"));
			job.setTitle(element.child(1).child(0).text());
			job.setName(job.getTitle());
			job.setLocation(element.child(2).child(1).text().trim());
			job.setCategory(element.child(3).child(1).text());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element descDiv = doc.getElementById("description_box");
		job.setReferenceId(descDiv.child(0).child(0).child(1).child(1).child(0).text());
		descDiv = doc.selectFirst("div.job_description");
		job.setSpec(descDiv.text());
		return job;
	}

	private int getTotalPage(String url) throws IOException {
		Document doc = Jsoup.connect(url.substring(0, 44)).get();
		this.baseUrl = doc.selectFirst("div[class=list_row_title]>a").attr("href");
		Element el = doc.select("span.total_results").get(0);
		String totalJob = el.text();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
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