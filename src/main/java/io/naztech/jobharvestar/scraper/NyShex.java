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
 * NyShex job site parser<br>
 * URL: http://jobs.nyshex.com/
 * 
 * @author Arifur Rahman
 * @author iftekar.alam
 * @since 2019-03-27
 */
@Service
@Slf4j
public class NyShex extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NYSHEX;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc=Jsoup.connect(siteMeta.getUrl()).get();
		Elements jobList=doc.select("h4[class=list-group-item-heading]>a");
		expectedJobCount=jobList.size();
		for (Element el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(el.attr("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of " + job.getUrl(),e);
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
