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
 * GoodRx jobsite<br>
 * URL: https://hire.withgoogle.com/public/jobs/goodrxcom?background=rgba(0%2C%200%2C%200%2C%200)&no_padding=1
 * 
 * @author Muhammad Bin Farook
 * @author iftekar.alam
 * @since 2019-03-12
 */
@Slf4j
@Service
public class GoodRx extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.GOODRX;
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
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		Elements rowList = doc.select("li[class=bb-public-jobs-list__job-item ptor-jobs-list__item]");
		expectedJobCount = rowList.size();
		for (Element el : rowList) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getElementsByTag("a").attr("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of " + job.getUrl());
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("ul[class=bb-jobs-posting__job-details-list]>li");
		job.setCategory(jobE.text().trim());
		jobE = doc.select("ul[class=bb-jobs-posting__job-details-list]>li").get(1);
		job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("div[class=bb-rich-text-editor__content ptor-job-view-description public-job-description]");
		job.setSpec(jobE.text().trim());
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
