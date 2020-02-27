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
 * DAI ICHI LIFE HOLDINGS job site parser <br>
 * URL: https://progres10.jposting.net/pgdai_ichi_life_staff/u/job.phtml
 * 
 * @author Rahat Ahmad
 * @author iftekar.alam
 * @since 2019-03-03
 */
@Slf4j
@Service
public class DaiLifeHoldings extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DAI_ICHI_LIFE_HOLDINGS;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.DAI_ICHI_LIFE_HOLDINGS));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 55);
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements jobList = doc.select("tr[valign=middle]>td>a");
		try {
			browseJobList(siteMeta, jobList);
		} catch (Exception e) {
			log.warn("Failed to parse job list page", e);
		}

	}

	private void browseJobList(SiteMetaData siteMeta, Elements jobList) throws InterruptedException {
		expectedJobCount = jobList.size();
		for (Element el : jobList) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.attr("href"));
			job.setTitle(el.text().trim());
			job.setName(job.getTitle());
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
		Element jobE = doc.selectFirst("span[class=jobname]");
		String location = "";
		if (jobE != null)
			location = jobE.text().trim();
		if (location.contains("〈"))
			job.setLocation(location.split("〈")[1].split("〉")[0]);
		Elements jobDetail = doc.select("table:has(table) > tbody > tr > td > table > tbody > tr");
		job.setSpec(jobDetail.get(3).child(1).wholeText().trim());
		job.setPrerequisite(jobDetail.get(4).child(1).wholeText().trim());
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