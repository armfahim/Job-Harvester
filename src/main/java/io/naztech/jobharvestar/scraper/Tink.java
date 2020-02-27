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
 * Tink Jobsite Parser<br>
 * URL: https://jobs.tink.se/jobs
 * 
 * @author fahim.reza
 * @since 2019-03-24
 */
@Service
@Slf4j
public class Tink extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TINK;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception { 
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 20);
		Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements jobEl = doc.select("a[class=u-primary-background-color ]");
		expectedJobCount = jobEl.size();
		for (Element element : jobEl) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = getBaseUrl() + element.attr("href");
			Job job = new Job(jobUrl);
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+ job.getUrl());
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document document = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(document.select("h1").text());
		job.setName(job.getTitle());
		job.setSpec(document.getElementsByClass("body u-margin-top--medium u-primary-text-color").text());
		job.setApplicationUrl(document.select("div.apply  > a").attr("href"));
		String location[] = document.getElementsByClass("byline u-primary-text-color").text().split("–");
		if (location.length > 1) {
			job.setCategory(location[0]);
			job.setLocation(location[1]);
		} else {
			job.setLocation(location[0]);
		}
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
