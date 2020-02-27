package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

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
 * Natixis bank site scrapper.<br>
 * URL: https://recrutement.natixis.com/en/jobs/list?page=
 * 
 * @author farzana.islam
 * @since 2019-01-28
 */
@Service
@Slf4j
public class NatixisBank extends AbstractScraper implements Scrapper {
	private static final String JOBSITE_HOST = "https://recrutement.natixis.com";
	private static final String HEADURL = "https://recrutement.natixis.com/en/jobs/list?page=";
	private static final String SITE = ShortName.NATIXIS;
	private Exception exception;

	private String baseUrl;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("M/dd/yyyy");
	private int expectedJobCount;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		int totalPage = getTotalPages(siteMeta);
		expectedJobCount = totalPage * 10;
		if (log.isTraceEnabled()) log.trace(SITE + " Total " + Integer.toString(totalPage) + " Pages to be Scrapped");
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = HEADURL + i;
			try {
				getSummaryPages(url, siteMeta);				
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalPages(SiteMetaData siteMeta) throws IOException {
		String[] str;
		int totalPage = 0;
		try {
			Document doc = Jsoup.connect(siteMeta.getUrl() + "?page=").get();
			Elements elTotalPageNo = doc.select("ul.pagination_custom:has(li) > li");
			str = elTotalPageNo.get(0).child(0).text().split("/");
			totalPage = Integer.parseInt(str[1].trim());
		} catch (IOException e) {
			log.error("Exception on getTotalPages " + e);
			throw e;
		}
		return totalPage;
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements list = doc.select("div.col-md-8 > ul > li");
		for (Element element : list) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(JOBSITE_HOST + element.child(0).attr("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.select("div#job >h1").get(0).wholeText());
		job.setName(job.getTitle());
		job.setReferenceId(doc.select("div.col-md-8:has(div)").get(0).child(0).child(1).text());
		job.setCategory(doc.select("div.col-md-8:has(div)").get(0).child(4).child(1).text());
		String dateVal = doc.select("div.col-md-8:has(div)").get(0).child(1).child(1).text();
		job.setPostedDate(parseDate(dateVal, DF));
		if (job.getPostedDate() == null) log.info(SITE + " failed to parse date value " + dateVal + " for job " + job.getUrl());
		String country = doc.select("div.col-md-8:has(div)").get(0).child(3).child(0).child(0).wholeText();
		String region = doc.select("div.col-md-8:has(div)").get(0).child(3).child(0).child(1).wholeText();
		job.setLocation(region.substring(country.indexOf(":") + 1).trim() +"," +country.substring(country.indexOf(":") + 1).trim());
		job.setApplyEmail(doc.select("a.link_purple").get(0).attr("href"));
		job.setSpec(doc.select("div#job_block_desc").get(0).wholeText());
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
