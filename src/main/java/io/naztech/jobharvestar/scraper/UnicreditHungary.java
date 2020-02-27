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
 * Unicredit Hungary 
 * URL: https://unicredit.terminal.lensa.hu/s/karrier-oldal
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-01-30
 */
@Service
@Slf4j
public class UnicreditHungary extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNICREDIT_HUNGARY;
	private String baseUrl;
	private static final String TAILURL = "/s/karrier-oldal?oldal=";
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.UNICREDIT_HUNGARY));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 35);
		int totalPage = getTotalPages(site.getUrl());
		expectedJobCount = totalPage * 10;
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + TAILURL + i;
			try {
				browseJobList(getBaseUrl() + TAILURL + i, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalPages(String url) throws IOException {
		return Jsoup.connect(url).get().select("div[class=pager_block]>a").size() + 1;
	}

	private void browseJobList(String url, SiteMetaData site) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("h3[class=job_title]>a");
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = getBaseUrl() + el.attr("href");
			try {
				saveJob(getJobDetail(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + jobUrl, e);
			}
		}
	}

	private Job getJobDetail(String jobUrl) throws IOException {
		Job job = new Job(jobUrl);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("a[class=general_button button_apply]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		Elements jobInfo = doc.select("div[class=formatted_text multicolumn]");
		job.setSpec(jobInfo.get(0).text());
		if (jobInfo.size() > 1) {
			String info = jobInfo.get(1).text();
			jobInfo = jobInfo.get(1).select("div[class=box]>b");
			if (jobInfo.size() == 5) {
				for (Element el : jobInfo) {
					info = info.replace(el.text(), "");
					String[] parts = info.split(":");
					job.setType(parts[1]);
					job.setLocation(parts[4]);
				}
			}
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
