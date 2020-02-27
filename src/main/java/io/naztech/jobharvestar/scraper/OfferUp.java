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
 * OfferUp Jobsite Parser.<br>
 * URL: https://about.offerup.com/careers/
 * 
 * @author Shadman.Shahriar
 * @since 2019-03-21
 */
@Slf4j
@Service
public class OfferUp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.OFFERUP;
	private int expectedJobCount = 0;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			Document document = Jsoup.connect(siteMeta.getUrl()).get();
			Elements nextPageUrl = document.getElementsByClass("col-md-4 col-xs-5");
			for (Element element : nextPageUrl) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Document nextPage = Jsoup.connect(element.select("a").attr("href")).get();
				browseJobList(nextPage,siteMeta);
	
		} 
		}catch (NullPointerException | IndexOutOfBoundsException e) {
			log.warn("Failed to find element ",e);
			throw e;
		}
	}

	private void browseJobList(Document nextPage, SiteMetaData siteMeta) throws InterruptedException {
		try {
			Elements categoryElements = nextPage.getElementsByClass("row row-eq-height align-items-center");
			expectedJobCount += categoryElements.size();
			for (Element element2 : categoryElements) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setTitle(element2.select("h3.c-green").text());
				job.setName(job.getTitle());
				job.setUrl(element2.getElementsByClass("btn btn-gray pull-right").attr("href"));
				try {
					saveJob(getJobDetails(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			log.warn("Failed to find element ",e);
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		try {
			Document document = Jsoup.connect(job.getUrl()).get();
			Elements jobDetails = document.select("div.text-editor >*");
			String details = "";
			for (int i = 0; i < jobDetails.size(); i++) {
				if (i==jobDetails.size()-1) job.setApplicationUrl(jobDetails.get(i).select("a").attr("href"));
				else details+=jobDetails.get(i).text();
				job.setSpec(details);
			}
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			log.warn("Failed to parse job details of "+job.getUrl(),e);
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
