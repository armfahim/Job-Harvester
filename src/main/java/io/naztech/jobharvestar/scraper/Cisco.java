package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.regex.Pattern;

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
 * Cisco Systems job site parser <br>
 * URL: https://jobs.cisco.com/jobs/SearchJobs/?source=Cisco+Jobs+Career+Site&tags=CDC+Browse+all+jobs+careers
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-03-06
 */
@Slf4j
@Service
public class Cisco extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CISCO_SYSTEMS;
	private String baseUrl;
	private static final int JOB_PER_PAGE = 25;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));

	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 40);
		Document doc = loadPage(site.getUrl());
		int totalPage = getTotalPage(doc);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "projectOffset=" + ((i * JOB_PER_PAGE) - JOB_PER_PAGE);
			try {
				doc = loadPage(url);
				Elements rowList = doc.select("table.table_basic-1 > tbody > tr");
				browseJobList(rowList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.select("a").attr("href"));
			job.setTitle(el.select("a").text());
			job.setName(job.getTitle());
			job.setCategory(el.select("td").get(1).text());
			job.setType(el.select("td").get(2).text());
			job.setLocation(el.select("td").get(3).text());
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		job.setSpec(loadPage(job.getUrl()).select("div.job_description").text());
		return job;
	}

	private int getTotalPage(Document doc) throws IOException {
		Element elTotalPage = doc.selectFirst("span[class=pagination_legend]");
		String totalJob = elTotalPage.text().split("of")[1].trim();
		if(totalJob.contains("+")) totalJob = totalJob.split(Pattern.quote("+"))[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOB_PER_PAGE);
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
