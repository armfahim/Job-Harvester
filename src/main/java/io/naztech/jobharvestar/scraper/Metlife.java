package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * METLIFE<br>
 * URL: "https://jobs.metlife.com/search"
 * 
 * @author tohedul.islum
 * @author iftekar.alam
 * @since 2019-02-10
 */
@Service
@Slf4j
public class Metlife extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.METLIFE;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static final String TAILURL = "/search?q=&sortColumn=referencedate&sortDirection=desc&startrow=";
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.METLIFE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		int totalJob = getTotalJobs(siteMeta.getUrl());
		expectedJobCount = totalJob;
		for (int i = 0; i < totalJob; i += 100) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + TAILURL + i;
			try {
				getSummaryPages(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}

	}

	private int getTotalJobs(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements pageNo = doc.select("span[class=paginationLabel]>b");
		return Integer.parseInt(pageNo.get(1).text());
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements list = doc.select("div[class=searchResultsShell]>table>tbody>tr");
		for (Element row : list) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Element link = row.getElementsByTag("td").get(0).getElementsByTag("a").get(0);
			Job job = new Job(getBaseUrl() + link.attr("href"));
			job.setTitle(link.text());
			job.setName(job.getTitle());
			job.setLocation(row.getElementsByTag("td").get(1).text());
			job.setPostedDate(parseDate(row.getElementsByTag("td").get(2).text().trim(), DF));
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
		Element jobE = doc.selectFirst("div[class=job]>span>ul");
		if (jobE == null) jobE = doc.selectFirst("div[class=job]");
		job.setSpec(jobE.text());
		jobE = doc.selectFirst("div[class=applylink pull-right]>a");
		job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
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
