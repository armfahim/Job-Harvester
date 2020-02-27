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
 * Gimi Jobsite Parser<br>
 * URL: https://gimi.teamtailor.com/
 * 
 * @author iftekar.alam
 * @since 2019-03-31
 */
@Service
@Slf4j
public class Gimi extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.GIMI;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.GIMI));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		this.baseUrl = siteMeta.getUrl().substring(0, 27);
		getSummaryPages(doc, siteMeta);
	}

	private void getSummaryPages(Document doc, SiteMetaData siteMeta) throws PageScrapingInterruptedException {
		Elements jobList = doc.select("div[class=job-listing-container]>ul>li>a");
		expectedJobCount = jobList.size();
		for (Element li : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + li.attr("href"));
			job.setTitle(li.select("div").select("span[class=title u-link-color u-no-hover]").get(0).text());
			job.setName(job.getTitle());
			job.setLocation(li.select("div").select("span[class=meta u-text--small u-primary-text-color  u-margin-left--auto ]").get(0).text().split("-")[1].trim());
			job.setCategory(li.select("div").select("span[class=meta u-text--small u-primary-text-color  u-margin-left--auto ]").get(0).text().split("-")[0].trim());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("faild to parse job details of "+getBaseUrl(),e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).get();
		Element spec=doc.selectFirst("div[class=body u-margin-top--medium u-primary-text-color]");
		job.setSpec(spec.text().trim());
		Element applicationUrl=doc.selectFirst("a[class=btn btn-apply u-primary-button-text-color u-primary-button-background-color ]");
		job.setApplicationUrl(applicationUrl.attr("href"));
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
