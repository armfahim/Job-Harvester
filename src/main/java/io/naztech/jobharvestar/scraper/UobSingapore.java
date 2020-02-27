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
 * United Overseas Bank Singapore.
 * URL: https://careers.uobgroup.com/search/?q=&sortColumn=referencedate&sortDirection=desc
 * 
 * @author benajir.ullah
 * @author tanmoy.tushar
 * @since 20109-01-27
 */
@Service
@Slf4j
public class UobSingapore extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNITED_OVERSEAS_BANKING_GROUP_SINGAPORE;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private String EXTENDED_URL = "/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.UNITED_OVERSEAS_BANKING_GROUP_SINGAPORE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 28);
		int totalPages = getTotalPages(siteMeta);
		browseJobList(siteMeta.getUrl(), siteMeta);
		for (int i = 2; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + EXTENDED_URL + (i * 20 - 20);
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalPages(SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements elements = doc.select("span.paginationLabel");
		String totalJob = elements.get(0).text().substring(17).trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 20);
	}

	private void browseJobList(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).timeout(TIME_10S).get();
		Elements sumEl = doc.getElementsByClass("data-row clickable");
		for (Element el : sumEl) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			job.setTitle(el.child(0).child(0).child(0).text());
			job.setName(el.child(0).child(0).child(0).text());
			job.setUrl(this.baseUrl + el.child(0).child(0).child(0).attr("href"));
			job.setLocation(el.child(1).text());
			job.setType(el.child(2).text());
			job.setCategory(el.child(3).text());
			job.setPostedDate(parseDate(el.child(4).text(), DF));
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws InterruptedException, IOException {
		String jobDescription = null;
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements desE = doc.select("div.job");
		for (Element el : desE) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			jobDescription = el.wholeText();
		}
		job.setSpec(jobDescription);
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
