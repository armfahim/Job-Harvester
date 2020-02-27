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
 * Luxoft Jobsite Parser<br>
 * URL: https://career.luxoft.com/job-opportunities/?PAGEN_1=
 * 
 * @author iftekar.alam
 * @since 2019-10-21
 */
@Service
@Slf4j
public class Luxoft extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.LUXOFT;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(ShortName.LUXOFT));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl= siteMeta.getUrl().substring(0, 25);
		int i = 1;
		while (true) {
			String pageUrl = siteMeta.getUrl() + i;
			try {
				getSummaryPages(pageUrl, siteMeta);
			} catch (Exception e) {
				log.warn("failed to parse list of "+pageUrl,e);
			}
			Document doc = Jsoup.connect(pageUrl).timeout(TIME_10S).get();
			Element rowList = doc.select("ul[class=pagination]>li").get(6);
			String nestUrl = rowList.getElementsByTag("a").attr("href");
			if (nestUrl.contains("#")) break;
			i++;
		}
	}

	private void getSummaryPages(String url, SiteMetaData site) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).get();
		Elements jobList = doc.select("table[class=table table-jobs table-hover]>tbody>tr");
		expectedJobCount+=jobList.size();
		for (Element el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.getElementsByTag("td").get(1).getElementsByTag("a").attr("href"));
			job.setTitle(el.getElementsByTag("td").get(1).getElementsByTag("a").text().trim());
			job.setName(job.getTitle());
			job.setLocation(el.getElementsByTag("td").get(2).getElementsByTag("span").text().trim());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				log.warn("Faild to parse details of " + getBaseUrl(), e);
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements jobE = doc.select("div[class=white-background]");
		job.setSpec(jobE.get(0).text().trim() + jobE.get(1).text().trim());
		job.setPrerequisite(jobE.get(2).text().trim());
		jobE = doc.select("div[class=list-group-block-item-text]");
		if(jobE.size()==4) job.setReferenceId(jobE.get(3).text().trim());
		else if(jobE.size()==5) job.setReferenceId(jobE.get(4).text().trim());
		else if(jobE.size()==3) job.setReferenceId(jobE.get(2).text().trim());
		Element rowList = doc.selectFirst("a[class=btn btn-primary btn-block js-tracking]");
		job.setApplicationUrl(getBaseUrl()+rowList.attr("href"));
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
