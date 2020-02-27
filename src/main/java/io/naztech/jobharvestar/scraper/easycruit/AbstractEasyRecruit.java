package io.naztech.jobharvestar.scraper.easycruit;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Abstract Scraper for easyrecruit sites. <br>
 * Implementing class: Skand Enskilda Banken (Latvia, Lithuania, Sweden,
 * Denmark)
 * 
 * @author Armaan Seraj Choudhury
 * @author Rahat Ahmad
 * @author Tanbirul Hashan
 * @since 2019-02-27
 */
@Service
public abstract class AbstractEasyRecruit extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private int expectedJobCount;
	protected String baseUrl;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Override
	public void scrapJobs() throws Exception {
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled())
			log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			this.baseUrl=siteMeta.getUrl().substring(0, 27);
			Document doc = Jsoup.connect(siteMeta.getUrl()).get();
			Elements rowList = doc.select("div.joblist>div");
			expectedJobCount = rowList.size() - 1;
			for (int i = 1; i <= expectedJobCount; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + rowList.get(i).select("div>a").attr("href"));
				job.setName(rowList.get(i).select("div>a").text().trim());
				job.setTitle(job.getName());
				job.setLocation(rowList.get(i).getElementsByTag("div").get(2).getElementsByTag("div").text().split(":")[1].trim());
				job.setDeadline(parseDate(rowList.get(i).select("div[class=joblist-table-cell joblist-deadline]").text().split(":")[1].trim(),DF, DF1,DF2,DF3));
				System.out.println(job.getDeadline());
				job.setPostedDate(parseDate(rowList.get(i).select("div[class=joblist-table-cell joblist-posting-date]").text().split(":")[1].trim(),DF, DF1,DF2,DF3));
				System.out.println(job.getPostedDate());
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse details of " + job.getUrl());
				}
			}
		} catch (IOException e) {
			log.warn("Failed to load job list page", e);
			throw e;
		}
	}

	protected Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements descE = doc.select("div.jd-description");
		if (descE != null)
			job.setSpec(descE.text().trim());
		if (job.getSpec().isEmpty())
			log.warn("Description not found in " + job.getUrl());
		return job;
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

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	protected abstract void setBaseUrl(SiteMetaData site);
}
