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
 * SpaceX job site parser <br>
 * URL: https://www.spacex.com/careers/list
 *
 * @author a.s.m. tarek
 * @author tanmoy.tushar
 * @since 2019-03-11
 */
@Service
@Slf4j
public class SpaceX extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SPACEX;
	private int expectedJobCount;
	private Exception exception;
	private String baseUrl;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.SPACEX));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 35);
		getSummaryPage(getBaseUrl(), site);
	}

	private void getSummaryPage(String url, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("div.view-content").select("tbody").select("tr");
		expectedJobCount = rowList.size();
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getElementsByTag("a").attr("href"));
			job.setTitle(el.getElementsByTag("a").text().trim());
			job.setName(job.getTitle());
			job.setLocation(el.getElementsByTag("td").get(1).text().trim());
			try {
				saveJob(getJobDetail(job), site);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element elDes = doc.getElementById("content");
		job.setSpec(elDes.wholeText().trim());
		job.setApplicationUrl(job.getUrl() + "#app");
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
