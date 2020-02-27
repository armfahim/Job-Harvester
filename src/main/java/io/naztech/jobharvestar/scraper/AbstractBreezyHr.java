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
 * Abstract class for breezy.hr job site. <br>
 * 
 * @author tanmoy.tushar
 * @author bm.alamin
 * @since 2019-10-01
 */
public abstract class AbstractBreezyHr extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl();
		Document doc = Jsoup.connect(site.getUrl()).get();
		Elements jobList = doc.select(getRowListXPath());
		expectedJobCount = jobList.size();
		browseJobList(jobList, site);
	}

	private void browseJobList(Elements jobList, SiteMetaData site)	throws PageScrapingInterruptedException, IOException {
		for (Element el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(getBaseUrl() + el.attr("href")), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " +  getBaseUrl() + el.attr("href"), e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[id=description]").text().trim());
		Element jobE = doc.selectFirst("li[class=apply-button]>a");
		if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		jobE = doc.selectFirst("li[class=location]>span");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("li[class=type]>span");
		if (jobE != null) job.setType(jobE.text().trim().split("TYPE_")[1].split("%")[0].trim());
		return job;
	}
	
	protected String getRowListXPath() {
		return "li[class=position transition]>a";
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
