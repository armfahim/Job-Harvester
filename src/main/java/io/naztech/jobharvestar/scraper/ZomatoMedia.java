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
 * Zomato Media job site scraper. <br>
 * URL: https://zomato.recruitee.com/
 * 
 * @author a.s.m. tarek
 * @author tanmoy.tushar
 * @since 2019-03-14
 */
@Slf4j
@Service
public class ZomatoMedia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ZOMATO_MEDIA;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 28);
		try {
			getSummaryPage(site.getUrl(), site);
		} catch (Exception e) {
			log.warn("Failed to parse job list", e);
			throw e;
		}
	}

	private void getSummaryPage(String url, SiteMetaData site) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).get();
		Elements lists = doc.select("div.job");
		Elements jl = doc.select("a.col-md-6");
		expectedJobCount = lists.size();
		int i = 0;
		for (Element tr : lists) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + jl.get(i++).attr("href"));
			Element title = tr.selectFirst("h5.title");
			job.setTitle(title.text());
			job.setName(job.getTitle());
			Element dept = tr.selectFirst("div.department");
			job.setCategory(dept.text());
			Element loc = tr.selectFirst("li.location");
			job.setLocation(loc.text());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements spec = doc.select("div.description");
		job.setSpec(spec.get(0).text().trim());
		if (spec.size() > 1) {
			job.setPrerequisite(spec.get(1).text().trim());
		}
		Element appUrl = doc.selectFirst("a[class=btn btn-thebiggest]");
		if (appUrl != null) job.setApplicationUrl(getBaseUrl() + appUrl.attr("herf"));
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
