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
 * Bank Of Ireland job site parser. <br> 
 * URL: https://careers.bankofireland.com/jobs/search?utf8=%E2%9C%93&query=&button=
 * 
 * @author benajir.ullah
 * @author tanmoy.tushar
 * @since 20109-01-24
 */
@Service
@Slf4j
public class BankOfIreland extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANK_OF_IRELAND_GROUP;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	private static final DateTimeFormatter DF =  DateTimeFormatter.ofPattern("MMM dd, yyyy");
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.BANK_OF_IRELAND_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 46);
		int totalPage = getTotalPage(siteMeta.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "button=&page=" + i + "&query=&utf8=✓";
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalPage(String url) throws IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element totalJob1 = doc.select("div[class=table-counts]>p>b").get(0);
		String totalJob=totalJob1.text().split("all")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 30);
	}

	private void browseJobList(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("table.table > tbody > tr");
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.child(0).child(0).attr("href"));
			job.setName(el.child(0).child(0).text());
			job.setTitle(job.getName());
			job.setCategory(el.child(1).text());
			job.setType(el.child(2).text());
			job.setLocation(el.child(3).text());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setSpec(doc.selectFirst("div[class=job-description]").text());
		job.setDeadline(parseDate(doc.selectFirst("div[class=job-close-date]").text().split(":")[1].trim(),DF));
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
