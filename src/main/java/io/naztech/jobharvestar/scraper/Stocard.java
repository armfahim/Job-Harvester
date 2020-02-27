package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;
/**
 * Stocard Jobsite Parser<br>
 * URL: https://stocardapp.com
 * 
 * @author Kayumuzzaman Robin
 * @since 2019-04-01
 */
@Service
@Slf4j
public class Stocard extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.STOCARD;
	private String url = "https://stocardapp.com";
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
			Document document = Jsoup.connect(siteMeta.getUrl()).get();
			Elements jobEl = document.getElementsByClass("row full-width").select("a");
			Elements jobLoc = document.getElementsByClass("location light");
			expectedJobCount = jobEl.size();
			getSummaryPages(jobEl, jobLoc, siteMeta); 
		}

	private void getSummaryPages(Elements jobDivEl, Elements jobLoc, SiteMetaData siteMeta) {
		try {
			for (Element element : jobDivEl) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				String jobUrl = url + element.getElementsByTag("a").attr("href");
				Job job = new Job(jobUrl);
				job.setTitle(element.text());
				job.setName(job.getTitle());
				job.setLocation(jobLoc.get(0).text());
				try {
					saveJob(getJobDetails(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (PageScrapingInterruptedException e) {
			log.warn("Failed to parse job summary", e);
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		try {
			Document document = Jsoup.connect(job.getUrl()).get();
			Elements jobSpec = document.getElementsByClass("col-md-12");
			Elements jobsUrl = document.getElementsByClass("btn button-wide application-button");
			job.setSpec(jobSpec.get(3).text());
			job.setApplicationUrl(jobsUrl.attr("href"));
		} catch (ElementNotFoundException e) {
			log.warn("Failed to get job details of " + job.getUrl(), e);
		}
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
