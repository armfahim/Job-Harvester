package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * C2FO job site scraper. <br>
 * URL: https://c2fo.com/company/careers/
 * 
 * @author muhammad tarek
 * @author iftekar.alam
 * @since 2019-03-27
 */
@Service
@Slf4j
public class C2fo extends AbstractScraper implements Scrapper {
    private static final String SITE = ShortName.C2FO;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.C2FO));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		getSummaryPage(site.getUrl(), site);
	}

	private void getSummaryPage(String url, SiteMetaData site) throws IOException, PageScrapingInterruptedException {
			Document doc = Jsoup.connect(url).get();
			Elements lists=doc.select("div[class=elementor-text-editor elementor-clearfix]").get(1).getElementsByTag("a");
			expectedJobCount = lists.size();
			for (Element tr : lists) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				String jobLink = tr.attr("href");
				Job job = new Job(jobLink);
				job.setTitle(tr.text());
				job.setName(job.getTitle());
				try {
				saveJob(getJobDetail(job), site);
				}catch(Exception e) {
					exception = e;
				}
			}
	}

	private Job getJobDetail(Job job) throws IOException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Elements spec = doc.select("section[class=description]");
			job.setSpec(spec.text());
			Element category = doc.select("span[class=job-criteria__text job-criteria__text--criteria]").get(2);
			job.setCategory(category.text());
			Element type = doc.select("span[class=job-criteria__text job-criteria__text--criteria]").get(1);
			job.setType(type.text());
			Elements loc = doc.select("span[class=topcard__flavor topcard__flavor--bullet]");
			job.setLocation(loc.text());
			Elements ApplicationUrl = doc.select("div[class=topcard__content-right]").get(0).getElementsByTag("a");
			job.setApplicationUrl(ApplicationUrl.attr("href"));
		} catch (FailingHttpStatusCodeException e) {
			log.warn(getSiteName() + "Failed to parse job details", e);
		}
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
