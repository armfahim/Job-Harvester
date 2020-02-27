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
 * CME group job site scraper. <br>
 * URL: https://jobs.cmegroup.com/jobs/search
 * 
 * @author Farzana Islam
 * @author tanmoy.tushar
 * @since 2019-01-20
 */
@Slf4j
@Service
public class CmeGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CME_GROUP;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.CME_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		int i = 1;
		Document doc = null;
		do {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = siteMeta.getUrl() + "?page=" + i;
			try {
				doc = Jsoup.connect(url).get();
				Elements rowList = doc.select("div.jobs-section__item-title > h2 > a");
				browseJobList(siteMeta, rowList);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
			i++;
		} while(hasNext(doc));
	}

	private boolean hasNext(Document doc) {
		Element next = doc.selectFirst("span[class=next_page disabled]");
		if (next == null) return true;
		else return false;
	}

	private void browseJobList(SiteMetaData siteMeta, Elements rowList) throws InterruptedException {
		expectedJobCount += rowList.size();
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = el.attr("href");
			try {
				saveJob(getJobDetails(jobUrl), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + jobUrl, e);
			}
		}
	}	

	private Job getJobDetails(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		String loc = "";
		if (jobE.nextElementSibling() != null) loc = jobE.nextElementSibling().text();
		if (loc.contains("Location:")) job.setLocation(loc.split("Location:")[1].trim());
		jobE = doc.selectFirst("div[class=page-section--full]>div[class=page-section--full]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("a[class=cs_item_apply_button_link social_apply]");
		if (jobE != null && jobE.attr("id").contains("link_")) 
			job.setReferenceId(jobE.attr("id").split("link_")[1].trim());
		job.setApplicationUrl(job.getUrl() + "#start");
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
