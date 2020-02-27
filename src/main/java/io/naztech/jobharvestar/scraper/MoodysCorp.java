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
 * Moody's Corp job site scraper. <br>
 * URL: https://careers.moodys.com/jobs/
 * 
 * @author naym.hossain
 * @since 2019-01-24
 */
@Service
@Slf4j
public class MoodysCorp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MOODYS_CORP;
	private String baseUrl;
	private static final int JOBPERPAGE = 10;
	private static final String HEADURL = "/jobs?jobpage=";
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.MOODYS_CORP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 26);
		int totalPage = getTotalPage(site.getUrl(), site);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + HEADURL + i;
			try {
				getSummaryPages(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list of " + url, e);
			}
		}
	}

	private int getTotalPage(String url, SiteMetaData site) throws IOException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		Elements elTotalJob = doc.select("h3.results-count");
		String[] str = elTotalJob.get(0).text().split(" ");
		expectedJobCount = Integer.parseInt(str[0].trim());
		return getPageCount(str[0].trim(), JOBPERPAGE);
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements elLinks = doc.select("div.jobs > a");
		for (Element link : elLinks) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobLink = getBaseUrl() + link.attr("href");
			Job job = new Job(jobLink);
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements elJob = doc.select("h1.post__title");
		job.setTitle(elJob.get(0).child(0).text());
		job.setName(elJob.get(0).child(0).text());
		String[] str = elJob.get(0).ownText().split("-");
		job.setReferenceId(str[2].trim());
		Elements elJob2 = doc.select("div.post__meta");
		job.setType(elJob2.get(0).child(2).child(0).text());
		job.setPrerequisite(elJob2.get(0).child(2).child(1).text());
		job.setCategory(elJob2.get(0).child(1).text());
		Elements elDes = doc.select("div.post__content");
		job.setSpec(elDes.get(0).wholeText().trim());
		Elements elAppUrl = doc.select("div.header-buttons");
		job.setApplicationUrl(elAppUrl.get(0).child(0).attr("href"));
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
