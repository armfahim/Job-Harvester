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
 * Lincoln National Corp job site parser. <br>
 * URL: https://jobs.lincolnfinancial.com/search
 * 
 * @author nuzhat.tabassum
 * @author tanmoy.tushar
 * @since 2019-02-12
 */
@Slf4j
@Service
public class LincolnNationalCorp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.LINCOLN_NATIONAL_CORP;
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final String TAILURL = "/search?q=&sortColumn=referencedate&sortDirection=desc&startrow=";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.LINCOLN_NATIONAL_CORP));
	}
	
	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 33);
		int totalJob = getTotalJobs(siteMeta.getUrl());
		for (int count = 0; count < totalJob; count += 25) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + TAILURL + count;
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}
	
	private int getTotalJobs(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements pageNo = doc.select("span[class=paginationLabel]>b");
		return expectedJobCount = Integer.parseInt(pageNo.get(1).text().trim());
	}
	
	private void browseJobList(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements list = doc.select("tbody>tr");
		for (Element row : list) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Elements columns = row.getElementsByTag("td");
			Element link = columns.get(0).getElementsByTag("a").get(0);
			Job job = new Job(getBaseUrl() + link.attr("href"));
			job.setTitle(link.text());
			job.setName(job.getTitle());
			job.setLocation(columns.get(1).text());
			job.setCategory(columns.get(2).text());
			job.setPostedDate(parseDate(columns.get(3).text().trim(), DF1, DF2));
			try {
				saveJob(getJobDetails(job), siteMeta);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}
	
	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.selectFirst("span[class=jobdescription]").text().trim());	
		job.setApplicationUrl(getBaseUrl() + doc.selectFirst("div[class=applylink pull-right]>a").attr("href"));
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
