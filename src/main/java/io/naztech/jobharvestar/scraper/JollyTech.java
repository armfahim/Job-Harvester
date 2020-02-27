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
 * JollyTech job site parser<br>
 * URL: https://www.jollytech.com/company/jobs/positions/index.php
 * 
 * @author farzana.islam
 * @author tanmoy.tushar
 * @since 2019-03-05
 */
@Slf4j
@Service
public class JollyTech extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.JOLLY_INFORMATION_TECHNOLOGY;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.JOLLY_INFORMATION_TECHNOLOGY));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 25);
		Document doc = Jsoup.connect(siteMeta.getUrl()).timeout(TIME_1M).get();
		Elements rowList = doc.select("div[class=body-container body-container-bg]>p>a");
		expectedJobCount = rowList.size();
		for (Element el : rowList) {
			Job job = new Job(getBaseUrl() + el.attr("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).timeout(TIME_1M).get();
		Elements jobE = doc.select("div.body-container > p");
		job.setTitle(jobE.get(0).text().split("Title")[1].trim());
		job.setName(job.getTitle());
		job.setReferenceId(jobE.get(1).text().split("ID")[1].trim());
		job.setLocation(jobE.get(2).text().split("Location")[1].trim());
		job.setSpec(jobE.get(3).text() + "" + jobE.get(4).text() + "" + jobE.get(5).text() + "" + jobE.get(6).text()
				+ "" + jobE.get(7).text() + "" + jobE.get(8).text());
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
