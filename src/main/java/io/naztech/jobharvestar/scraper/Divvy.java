package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Divvy Job Perser<br>
 * URL:
 * https://usr55.dayforcehcm.com/CandidatePortal/en-US/motivate/site/divvycareers
 * 
 * @author rafayet.hossain
 * @since 2019-03-31
 */
@Slf4j
@Service
public class Divvy extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DIVVY;
	private String baseUrl;
	private int expectedJobCount;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern(" MMMM d yyyy");
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 29);
		try {
			browseJobList(site);
		} catch (Exception e) {
			log.warn("Failed to parse list of "+site.getUrl());
		}
	}

	public void browseJobList(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document document = Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements el = document.select("li[class=search-result]");
		expectedJobCount = el.size();
		for (int i = 0; i < el.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			Elements title = el.get(i).select("div[class=posting-title]");
			Elements location = el.get(i).select("div[class=location]");
			Elements postDate = el.get(i).select("div[class=posting-date]");
			Elements applyUrl = el.get(i).select("div.posting-actions>a");
			Elements DetailsPageUrl = el.get(i).select("div.posting-description>a");
			job.setTitle(title.text());
			job.setName(job.getTitle());
			job.setLocation(location.text());
			job.setPostedDate(parseDate(postDate.text().split(",")[1] + postDate.text().split(",")[2], DF));
			job.setApplicationUrl(baseUrl + applyUrl.attr("href"));
			job.setUrl(baseUrl + DetailsPageUrl.attr("href"));
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse list of "+job.getUrl());
			}
		}
	}

	public Job getJobDetails(Job job) throws IOException {
		Document document = Jsoup.connect(job.getUrl()).get();
		Elements description = document.select("div[class=job-posting-content]");
		job.setSpec(description.text());
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