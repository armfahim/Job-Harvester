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
 * Wealthfront jobs site parse Url: https://www.wealthfront.com/careers
 * 
 * @author Kowshik Saha
 * @since 2019-04-02
 */
@Slf4j
@Service

public class Wealthfront extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.WEALTHFRONT;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		getSummaryPage(site);
	}

	private void getSummaryPage(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		try {
			Document doc = Jsoup.connect(site.getUrl()).get();
			Elements e = doc.select("#job-board > div > div > div > div.static-guest-careers-open-positions-section > ul > li > ul > li");
			expectedJobCount = e.size();
			for (Element element : e) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(element), site);
				} catch (Exception e2) {
					exception = e2;
				}
			}
		}catch (IOException e) {
			log.warn("Failed parse job details of", e);
			throw e;
		}
	}

	private Job getJobDetails(Element el) {
		String url = el.select("a").first().attr("href");
		Job job = new Job(url);
		try {
			Document doc = Jsoup.connect(url).get();
			Element jobSec = doc.select("body > div.content-wrapper.posting-page > div").first();
			job.setTitle(el.text());
			job.setName(job.getTitle());
			job.setApplicationUrl(jobSec.selectFirst("a[class=postings-btn template-btn-submit teal]").attr("href"));
			job.setLocation(jobSec.selectFirst("div[class=sort-by-time posting-category medium-category-label]").text());
			job.setCategory(jobSec.selectFirst("div[class=sort-by-team posting-category medium-category-label]").text());
			job.setSpec(jobSec.selectFirst("div[class=section-wrapper page-full-width]").text());
			return job;
		} catch (IOException e) {
			log.warn("Failed parse job details of" + job.getUrl(), e);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
