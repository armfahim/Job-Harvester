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
 * BNP Paribas job site scrapper.<br>
 * URL: https://group.bnpparibas/en/careers/offers-world-all
 * 
 * @author Naym Hossain
 * @author tanmoy.tushar
 * @since 2019-01-16
 */
@Service
@Slf4j
public class BnpParibas extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BNP_PARIBAS;
	private String baseUrl;
	private final int jobPerPage = 10;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.BNP_PARIBAS));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		int totalPage = getTotalPages(siteMeta);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = siteMeta.getUrl() + "?page=" + i;
			try {
				browseJobList(siteMeta, url);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(SiteMetaData siteMeta, String url) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements elTotalPageNo = doc.select("ul[class=results rh-results]>li>a");
		for (Element element : elTotalPageNo) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = getBaseUrl() + element.attr("href");
			try {
				saveJob(getJobDetail(jobUrl), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail page of " + jobUrl, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		Element jobE = doc.selectFirst("div[class=offer-info offer-info-type]>span"); 
		if (jobE != null) job.setType(jobE.text().trim());
		jobE = doc.selectFirst("div[class=offer-info offer-info-loc]>span");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("div[class=apply]>a");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("div[class=offer-info offer-info-domain]>span");
		if (jobE != null) job.setCategory(jobE.text().trim());
		job.setSpec(doc.selectFirst("div[class=description]").text().trim());
		return job;
	}

	private int getTotalPages(SiteMetaData siteMeta) throws IOException {
		try {
			Document doc = Jsoup.connect(siteMeta.getUrl() + "?page=1").userAgent(USER_AGENT).timeout(TIME_1M).get();
			Element el = doc.selectFirst("span[class=nb-total]");
			String totalJob = el.text().trim();
			expectedJobCount = Integer.parseInt(totalJob);
			return getPageCount(totalJob, jobPerPage);
		} catch (IOException e) {
			log.error("Failed to parse total job, Site exiting....", e);
			throw e;
		}
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
