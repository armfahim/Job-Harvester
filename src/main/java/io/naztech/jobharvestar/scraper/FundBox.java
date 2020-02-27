package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * FundBox job site scraper. <br>
 * URL: https://fundbox.com/careers/
 * 
 * @author muhammad tarek
 * @author iftekar.alam
 * @since 2019-04-02
 */
@Service
@Slf4j
public class FundBox extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.FUNDBOX;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 19);
		getSummaryPages(site);
	}

	private void getSummaryPages(SiteMetaData site) throws InterruptedException, IOException{
		Document doc=Jsoup.connect(site.getUrl()).get();
		Elements jobList = doc.select("li[class=Careers_Openings-job]");
		expectedJobCount=jobList.size();
		for (Element el: jobList) {
			Job job=new Job(getBaseUrl() + el.getElementsByTag("a").attr("href"));
			job.setTitle(el.text().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), site);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+job.getUrl(),e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).ignoreHttpErrors(true).get();
		Elements loc=doc.select("h1[class=CareersJob_Job-title]>span");
		job.setLocation(loc.get(2).text().trim());
		Element spec = doc.selectFirst("div[class=CareersJob_Job-description-text]");
		job.setSpec(spec.text().trim());
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
