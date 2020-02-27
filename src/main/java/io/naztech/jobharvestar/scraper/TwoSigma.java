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
 * Two Sigma job site parsing class. <br>
 * URL: https://careers.twosigma.com/
 * 
 * @author assaduzzaman.sohan
 * @author tanmoy.tushar
 * @since 2019-03-04
 */
@Slf4j
@Service
public class TwoSigma extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TWO_SIGMA;
	private String baseUrl;
	
	private static final int JOBS_PER_PAGE = 10;
	private int expectedJobCount = 0;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 28);
		Document doc = loadPage(site.getUrl());
		int totalPage = getTotalPage(doc);
		Elements rowList = doc.select("li[class=jobResultItem]>h3>a[class=mobileShow]");
		browseJobList(rowList, site);
		for(int i = 1; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/careers/SearchJobs/?jobOffset=" + (i * JOBS_PER_PAGE);
			try {
				doc = loadPage(url);
				rowList = doc.select("li[class=jobResultItem]>h3>a[class=mobileShow]");
				browseJobList(rowList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, SiteMetaData site) throws InterruptedException {
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = el.attr("href");
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
		job.setTitle(doc.selectFirst("h2[itemprop=title]").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=jobDetailDescription]").text().trim());
		Element jobE = doc.selectFirst("p[itemprop=jobLocation]");
		job.setLocation(jobE.text());
		String refId = jobE.nextElementSibling().text();
		if (refId.contains("Ref#")) job.setReferenceId(refId.split(":")[1].trim());
		jobE = doc.selectFirst("a[class=buttonLike rMar1]");
		job.setApplicationUrl(jobE.attr("href"));
		return job;
	}
	
	private int getTotalPage(Document doc) {
		String totalJob = doc.selectFirst("span[class=jobPaginationLegend]").text().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBS_PER_PAGE);
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
