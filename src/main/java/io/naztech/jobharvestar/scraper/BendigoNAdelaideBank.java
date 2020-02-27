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
 * Bendigo N Adelaide Bank job site parsing class. <br>
 * URL: http://jobs.bendigobank.com.au/caw/en/listing
 * 
 * @author tanmoy.tushar
 * @since 2019-02-14
 */
@Service
@Slf4j
public class BendigoNAdelaideBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BENDIGO_N_ADELAIDE_BANK;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final String ROW_LIST_PATH = "tbody[id=recent-jobs-content]>tr>td>a";
	private static final int JOB_PER_PAGE = 20;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.BENDIGO_N_ADELAIDE_BANK));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException, IOException {
		this.baseUrl = site.getUrl().substring(0, 30);
		Document doc = loadPage(site.getUrl());
		int totalPage = getTotalJob(doc) + 1;
		Elements rowList = doc.select(ROW_LIST_PATH);
		browseJobList(rowList, site);
		for (int i = 2; i <= totalPage; i++) {
			String url = site.getUrl() + "/?page=" + i;
			try {
				doc = loadPage(url);
				rowList = doc.select(ROW_LIST_PATH);
				browseJobList(rowList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements jobList, SiteMetaData site) throws InterruptedException {
		for (Element row : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + row.attr("href"));
			job.setTitle(row.text().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = loadPage(job.getUrl());
		job.setSpec(doc.selectFirst("div[id=job-details]").text().trim());
		Element jobE = doc.selectFirst("span[class*=work-type]");
		if (jobE != null) job.setType(jobE.text().trim());
		jobE = doc.selectFirst("span[class=location]");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("span[class=categories]");
		if (jobE != null) job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("span[class=open-date]>time");
		if (jobE != null) job.setPostedDate(parseDate(jobE.text().trim(), DF));
		jobE = doc.selectFirst("span[class=close-date]>time");
		if (jobE != null) job.setDeadline(parseDate(jobE.text().trim(), DF));
		jobE = doc.selectFirst("a[class=apply-link button]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		return job;
	}

	public int getTotalJob(Document doc) {
		Element el = doc.selectFirst("a[class=more-link button]>span");
		if (el == null) return 0;
		String totalJob = el.text().trim();
		expectedJobCount = Integer.parseInt(totalJob) + 20;
		return getPageCount(totalJob, JOB_PER_PAGE);
	}

	private Document loadPage(String url) throws IOException {
		return Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
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
