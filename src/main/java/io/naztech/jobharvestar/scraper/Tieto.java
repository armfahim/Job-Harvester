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
 * Tieto job site parser. <br>
 * URL: https://www.tieto.com/en/careers/open-positions/search-our-jobs/
 * 
 * @author tanmoy.tushar
 * @since 2019-10-20
 */
@Slf4j
@Service
public class Tieto extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TIETO;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMMM yyyy");
	private static final String ROW_LIST = "a[class=jobResultRow listingRow row loadingOverlay]";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 21);
		Document doc = loadPage(site.getUrl());
		Elements rowList = doc.select(ROW_LIST);
		browseJobList(rowList, site);
		while (true) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			if (getNextPageUrl(doc) == null) break;
			String url = getBaseUrl() + getNextPageUrl(doc);
			try {
				doc = loadPage(url);
				rowList = doc.select(ROW_LIST);
				browseJobList(rowList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		expectedJobCount += rowList.size();
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + el.attr("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = loadPage(job.getUrl());
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=row justify-content-md-center]>div").text().trim());
		Element jobE = doc.selectFirst("a[class=blueBtn roundedBtn float-lg-left mb-2]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		Elements jobInfo = doc.select("div[class=infobox]>p");
		for (Element el : jobInfo) {
			String txt = el.text();
			if (txt.contains("Location")) job.setLocation(txt.split(":")[1].trim());
			if (txt.contains("Area")) job.setCategory(txt.split(":")[1].trim());
			if (txt.contains("period")) job.setPostedDate(parseDate(txt.split(":")[1].trim().split(" - ")[0].trim(), DF));
		}
		return job;
	}

	private String getNextPageUrl(Document doc) {
		Element el = doc.selectFirst("a[id=jobListingPageLoadMore]");
		if (el == null)	return null;
		return el.attr("data-url");
	}

	private Document loadPage(String url) throws IOException {
		return Jsoup.connect(url).timeout(TIME_1M).ignoreHttpErrors(true).get();
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
