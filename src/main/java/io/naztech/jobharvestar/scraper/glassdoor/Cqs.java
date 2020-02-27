package io.naztech.jobharvestar.scraper.glassdoor;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * CQS job parsing class<br>
 * URL: https://www.totaljobs.com/jobs-at/cqs/jobs
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-10
 */
@Service
@Slf4j
public class Cqs extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CQS;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		this.baseUrl = siteMeta.getUrl().substring(0, 25);
		Elements jobList = doc.select("div[class=job-title]>a");
		expectedJobCount = jobList.size();
		for (Element el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.attr("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				log.warn("Faild to parse details of " + getBaseUrl(), e);
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=job-description]").text().trim());
		job.setLocation(doc.selectFirst("li[class=location icon]>div").text().trim());
		job.setReferenceId(doc.select("ul[class=contact-reference hidden-xs]>li").get(2).text().trim().split(":")[1].trim());
		job.setApplicationUrl(getBaseUrl()+doc.selectFirst("a[class=btn btn-exlg btn-primary brand-font apply-now-btn]").attr("href"));
		job.setPostedDate(parseAgoDates(doc.selectFirst("li[class=date-posted icon]>div>span").text().trim().split("Posted")[1].trim()));
		/*
		 * Element title = doc.selectFirst("h1"); job.setTitle(title.text().trim());
		 * job.setName(job.getName()); Elements tl =
		 * doc.select("dl[class=job-details list-details]");
		 * job.setLocation(tl.get(0).getElementsByTag("dd").get(0).text()); if
		 * (tl.get(0).getElementsByTag("dd").get(4).text().contains(","))
		 * job.setType(tl.get(0).getElementsByTag("dd").get(4).text().split(",")[0].trim
		 * ()); else job.setType(tl.get(0).getElementsByTag("dd").get(4).text().trim());
		 * Element el =
		 * doc.selectFirst("div[class=entry-content entry-content-single]");
		 * job.setSpec(el.text().trim());
		 */
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
