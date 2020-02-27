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
 * Royal Bank of Scotland.<br>
 * URL: https://jobs.rbs.com/search/jobs?q=
 * 
 * @author naym.hossain
 * @author sohid.ullah
 * @author bm.alamin
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-01-23
 */
@Slf4j
@Service
public class RoyalBankScotland extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ROYAL_BANK_OF_SCOTLAND;
	private String baseUrl;
	private static final String HEADURL = "/search/jobs/in?page=";
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.ROYAL_BANK_OF_SCOTLAND));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 20);
		Document doc = loadPage(siteMeta.getUrl());
		int totalPage = getTotalPages(doc);
		browseJobList(doc, siteMeta);
		for (int i = 2; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + HEADURL + i;
			try {
				doc = loadPage(url);
				browseJobList(doc, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job page no " + url, e);
			}
		}
	}

	private int getTotalPages(Document doc) throws IOException {
		Element el = doc.select("span[class=search-page__counter]").get(1);
		String totalJob = el.text().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 25);
	}

	private void browseJobList(Document doc, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Elements elJobList = doc.select("a[class=job__link]");
		for (Element el : elJobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.attr("href"));
			job.setPostedDate(parseAgoDates(el.getElementsByTag("div").get(3).getElementsByTag("div").get(2).text().split("Posted")[1].trim()));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getName());
		job.setSpec(doc.selectFirst("section[class=job-page__description body-copy]").text().trim());
		Element jobE = doc.selectFirst("span[class=job-page__detail job-page__location]");
		if(jobE !=null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("span[class=job-page__detail job-page__contract]");
		if(jobE !=null) job.setType(jobE.text().trim());
		jobE = doc.selectFirst("span[class=job-page__detail job-page__family]");
		if(jobE !=null) job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("div[class=job-page__reference]");
		if(jobE !=null) job.setReferenceId(jobE.text().split(":")[1].trim());
		jobE = doc.selectFirst("a[class=cs_item_apply_button_link]");
		if(jobE !=null) job.setApplicationUrl(jobE.attr("href"));
		return job;
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
