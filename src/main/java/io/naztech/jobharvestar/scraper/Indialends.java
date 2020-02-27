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
 * Indialends job site parser.<br>
 * URL: https://www.aasaanjobs.com/s/indialends-jobs/
 * 
 * @author alif.choyon
 * @author tanmoy.tushar
 * @since 2019-04-02
 */
@Service
@Slf4j
public class Indialends extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INDIALENDS;
	private static final int JOB_PER_PAGE = 10;
	private static final String ROW_LIST = "p[class=m-bottom-0 inline-flex-center flex-align-start]>a";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
		
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 26);
		Document doc = loadPage(siteMeta.getUrl());
		int totalPages = getTotalPages(doc);
		Elements jobList = doc.select(ROW_LIST);
		browseJobList(jobList, siteMeta);
		for (int i = 2; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = siteMeta.getUrl() + "page?=" + i;
			try {
				doc = loadPage(url);
				jobList = doc.select(ROW_LIST);
				browseJobList(jobList, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (Element el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + el.attr("href");
			try {
				saveJob(getJobDetails(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetails(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("p[itemprop = jobLocation]");
		job.setLocation(jobE.text().trim());
		Elements jobSpec = doc.select("div[class = row m-y-axis-sm]");
		job.setSpec(jobSpec.get(1).text().trim() + "\n" + jobSpec.get(3).text().trim());
		job.setPrerequisite(jobSpec.get(2).text().trim());
		return job;
	}

	private int getTotalPages(Document doc) {
		Element el = doc.selectFirst("span[class=text-gray-dark text-small text-right]");
		String totalJob = el.text().split("of")[1].trim().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOB_PER_PAGE);
	}
	
	private Document loadPage(String url) throws IOException {
		return Jsoup.connect(url).get();
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
