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
 * Deutsche Borse Group URL:
 * https://career.deutsche-boerse.com/search/?q=&sortColumn=referencedate&sortDirection=desc
 * 
 * @author Benajir Ullah
 * @author iftekar.alam
 * @since 2019-01-20
 */
@Slf4j
@Service
public class DeutscheBorseGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DEUTSCHE_BÖRSE_GROUP;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	private static DateTimeFormatter DF1= DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("dd.MM.yyyy");


	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 34);
		int totalJob = getTotalJob(siteMeta.getUrl());
		for (int i = 0; i <= totalJob; i=i+25) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = siteMeta.getUrl() + "&startrow=" + i;
			try {
				getSummaryPage(url, siteMeta);				
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalJob(String Url) throws IOException {
		Document doc = Jsoup.connect(Url).get();
		Element totalPage = doc.select("span[class=paginationLabel]").get(0);
		String pfc= totalPage.text().split("von")[1].trim();
		expectedJobCount = Integer.parseInt(pfc);
		return getExpectedJob();
	}

	private void getSummaryPage(String url,SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements list = doc.select("div.searchResultsShell > table > tbody > tr");
		for (Element el : list) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.select("span[class=jobTitle visible-phone]>a").attr("href"));
			job.setTitle(el.select("span[class=jobTitle visible-phone]>a").text());
			job.setName(job.getTitle());
			job.setLocation(el.select("span[class=jobLocation visible-phone]>a").text());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws InterruptedException, IOException {
		Document doc=Jsoup.connect(job.getUrl()).get();
		Element postTimeE = doc.getElementById("job-date");
		if (postTimeE != null && postTimeE.text().contains(":"))
			job.setPostedDate(parseDate(postTimeE.text().split(":")[1].trim(),DF1,DF2));
		Elements jobType = doc.select("div[class=boerse_jobtemplate]>p");
		if (jobType.size() > 2)	job.setType(jobType.get(2).getElementsByTag("span").get(0).text());
		Element jobF = doc.selectFirst("div[class=job]");
		job.setSpec(jobF.text());
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
