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
 * Facebook job site parsing class. <br>
 * URL: https://www.facebook.com/careers/jobs
 * 
 * @author assaduzzaman.sohan
 * @author tanmoy.tushar
 * @since 2019-03-06
 */
@Slf4j
@Service
public class Facebook extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.FACEBOOK;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 24);
		int totalPage = getTotalPage(site.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/careers/jobs?page=" + i;
			try {
				browseJobList(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("div[id=search_result]>a");
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.attr("href"));
			job.setCategory(el.selectFirst("div>div>div>div>div>div>div>div>div>div").text().trim());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE ;
		job.setTitle( doc.selectFirst("h4").text().trim());
		job.setName(job.getTitle());
		Elements details = doc.select("div[class=_8mlh]");
		if(details.size()==3)
		{
			job.setSpec(details.get(0).text().trim());
			job.setPrerequisite(details.get(1).text().trim()+" "+details.get(2).text().trim());
		}
		jobE = doc.selectFirst("span[class=_8lfp]");
		if(jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("a[class=_42ft _1p05 _2t6c _5kni _3nu9 _3nua _8q0b _8lfr _8lfg]");
		if(jobE != null) job.setApplicationUrl(getBaseUrl()+jobE.attr("href"));
		return job;
	}

	private int getTotalPage(String url) throws IOException  {
		try {
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Element el = doc.selectFirst("div[id=search_result]>div>div[class=_6ci_]");
			String totalJob = el.text().split("\\(")[1].split("\\)")[0].trim();
			if(totalJob.contains(",")) totalJob = el.text().split("\\(")[1].split("\\)")[0].replace(",", "").trim();
			expectedJobCount = Integer.parseInt(totalJob);
			return getPageCount(totalJob, 10);
		} catch (IOException e) {
			log.error("Failed to parse total job, site exiting....", e);
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
