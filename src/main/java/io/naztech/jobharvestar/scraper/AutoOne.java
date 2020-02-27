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
 * Auto One Group job site parser. <br>
 * URL: https://www.auto1-group.com/jobs/?page=1
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-11
 * 
 * @author tanmoy.tushar
 * @since 2019-04-18
 */
@Service
@Slf4j
public class AutoOne extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AUTO1_GROUP;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.AUTO1_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 27);
		int totalPages = getTotalPages(siteMeta);
		for (int i = 1; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/jobs/?page=" + i;
			try {
				getSummaryPage(siteMeta, url);
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private int getTotalPages(SiteMetaData siteMeta) throws IOException {
		try {
			Document doc = Jsoup.connect(siteMeta.getUrl()).get();
			Element el = doc.selectFirst("h3.h3");
			String totalJob = el.text().split(" ")[0].trim();
			expectedJobCount = Integer.parseInt(totalJob);
			return getPageCount(totalJob, 15);
		} catch (IOException e) {
			log.error("Failed to parse total page count", e);
			throw e;
		}
	}

	private void getSummaryPage(SiteMetaData siteMeta, String url) throws InterruptedException {
		try {
			Document doc = Jsoup.connect(url).get();
			Elements rowList = doc.select("a[class=container]");
			for (Element row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(row.attr("href"));
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch(Exception e) {
					exception = e;
					log.warn("Failed to parse job deatail of " + job.getUrl(), e);
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse job list", e);
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h4[class=title]");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div.row");
		job.setSpec(jobE.text().trim());
		job.setApplicationUrl(job.getUrl() + "apply/");
		jobE = doc.selectFirst("p[class=location-department]");
		String[] parts = jobE.text().split("\\|");
		job.setCategory(parts[0].trim());
		job.setLocation(parts[1].trim());
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
