package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Abstract for Jobvite sites. <br>
 * 
 * <a href="https://jobs.jobvite.com/xo-powered-by-jetsmarter/jobs">Jet Smarter</a>
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-03-14
 */
public abstract class AbstractJobvite extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		SiteMetaData site = getSiteMetaData(getSiteName());
		setBaseUrl(site);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		expectedJobCount = doc.select("td[class=jv-job-list-name]>a").size();
		Elements catList = doc.select("h3[class=h2]");
		for (Element cat : catList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			browseJobList(cat, site);
		}
	}

	private void browseJobList(Element cat, SiteMetaData site) throws PageScrapingInterruptedException {
		Elements rowList = cat.nextElementSibling().getElementsByTag("tr");
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Element title = el.selectFirst("td[class=jv-job-list-name]>a");
			Job job = new Job(getBaseUrl() + title.attr("href"));
			job.setTitle(title.text().trim());
			job.setName(job.getTitle());
			job.setLocation(el.selectFirst("td[class=jv-job-list-location]").text().trim());
			job.setCategory(cat.text().trim());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	protected Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.selectFirst("div[class=jv-job-detail-description]").text().trim());
		job.setApplicationUrl(getBaseUrl() + doc.selectFirst("a[class=jv-button jv-button-primary jv-button-apply]").attr("href"));
		return job;
	}

	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 24);
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
