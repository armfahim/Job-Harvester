package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

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
 * AIB Group URL:
 * https://jobs.aib.ie/search/?createNewAlert=false&q=&locationsearch=
 * 
 * @author Armaan Seraj Choudhury
 * @author iftekar.alam
 * @author fahim.reza
 * @since 2019-02-11
 */
@Slf4j
@Service
public class AibGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AIB_GROUP;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.AIB_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		this.baseUrl = siteMeta.getUrl().substring(0, 19);
		Element totalJob = doc.selectFirst("span[class=paginationLabel]");
		String totalJobF = totalJob.text().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJobF);
		Elements jobList = doc.select("span[class=jobTitle visible-phone]>a");
		for (Element el : jobList) {
			String url = getBaseUrl() + el.attr("href");
			try {
				saveJob(getJobDetail(new Job(url)), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details page of " + url, e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobTitle = doc.selectFirst("h1[id=job-title]");
		job.setTitle(jobTitle.text().trim());
		job.setName(job.getTitle());
		Element jobLoction = doc.selectFirst("span[class=jobGeoLocation]");
		job.setLocation(jobLoction.text().trim());
		Element jobPostedDate = doc.selectFirst("p[id=job-date]");
		job.setPostedDate(parseDate(jobPostedDate.text().split(":")[1].trim(), DF));
		Element jobAppUrl = doc.selectFirst("div[class=applylink pull-right]>a");
		job.setApplicationUrl(getBaseUrl()+jobAppUrl.attr("href").trim());
		Element jobSpec = doc.selectFirst("div[class=job]");
		job.setSpec(jobSpec.text().trim());
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
