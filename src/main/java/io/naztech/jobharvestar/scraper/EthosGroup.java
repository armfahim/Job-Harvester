package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Ethos Group jobsite pareser.<br>
 * Url: http://www.ethosgroup.com/careers/
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-04-02
 */
@Slf4j
@Service
public class EthosGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ETHOS;
	private static String HEAD = "https://ethosgroup.secure.force.com";
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<Job> jobList = getSummaryPage(siteMeta.getUrl());
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private List<Job> getSummaryPage(String url) throws PageScrapingInterruptedException, IOException {
		Document summaryPage = Jsoup.connect(url).get();
		Elements jobEl = summaryPage.select("a.btn");
		List<Job> jobList = new ArrayList<>();
		for (int i = 3; i < jobEl.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				Document doc = Jsoup.connect(jobEl.get(i).attr("href")).get();
				Elements jobListE = doc.select("table.atsSearchResultsTable > tbody > tr");
				for (int j = 0; j < jobListE.size(); j++) {
					if (isStopped()) throw new PageScrapingInterruptedException();
					Job job = new Job(HEAD + jobListE.get(j).select("a").attr("href"));
					job.setTitle(jobListE.get(j).select("a").text().trim());
					job.setName(job.getTitle());
					job.setCategory(jobListE.get(j).select("td").get(1).select("span").text());
					job.setLocation(jobListE.get(j).select("td").get(2).select("span").text());
					jobList.add(job);
				}
			} catch (IOException e) {
				log.warn("Failed to parse job summary of " + jobEl.get(i).attr("href"), e);
			}
		}
		return jobList;
	}

	private Job getJobDetails(Job job) throws IOException {
		Document detailPage = Jsoup.connect(job.getUrl()).get();
		job.setSpec(detailPage.select("td.atsJobDetailsTdTwoColumn").text());
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
