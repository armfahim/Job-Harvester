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
 * MUFG Indonesia jobs site parse.<br>
 * URL: https://www.mufg.co.id/career
 * 
 * @author Armaan Seraj Choudhury
 * @author iftekar.alam
 * @since 2019-01-23
 */
@Service
@Slf4j
public class MufgIndonesia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MUFG_INDONESIA;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		getSummaryPage(siteMeta);
	}

	private void getSummaryPage(SiteMetaData siteMeta) throws InterruptedException, IOException {
		String url = siteMeta.getUrl();
		try {
			@SuppressWarnings("deprecation")
			Elements list = Jsoup.connect(url).validateTLSCertificates(false).get().select("li.has-child-menu-career");
			expectedJobCount = list.size();
			for (Element el : list) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				String joburl = el.child(0).attr("href");
				Job job = new Job(joburl);
				job.setTitle(el.child(0).text());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
			throw e;
		}
	}

	private Job getJobDetails(Job job) throws InterruptedException, IOException {
		Thread.sleep(5000);
		@SuppressWarnings("deprecation")
		Document doc = Jsoup.connect(job.getUrl()).validateTLSCertificates(false).get();
		job.setSpec(doc.select("div.description-area").text());
		job.setPrerequisite(doc.select("div.description-area > ul").text());
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