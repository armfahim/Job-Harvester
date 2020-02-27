package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * TRAVELERS COS THE job site parsing class. <br>
 * URL: https://careers.travelers.com/job-search-results/
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-27
 */
@Slf4j
@Service
public class TravelersCosThe extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TRAVELERS_COS_THE;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	private static final int JOBS_PER_PAGE = 12;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 29);

		HtmlPage page;
		CLIENT = getFirefoxClient();
		page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScriptStartingBefore(TIME_10S);
		CLIENT.waitForBackgroundJavaScript(TIME_5S);

		int totalPage = getPageCount(page.getBody().getOneHtmlElementByAttribute("span", "id", "live-results-counter").getTextContent().trim(), JOBS_PER_PAGE);
		List<String> allPageLink = getAllPageLink(totalPage);

		getSummaryPages(page, siteMeta);
		for (int i = 0; i < allPageLink.size(); i++) {
			if (isStopped())throw new PageScrapingInterruptedException();

			page = CLIENT.getPage(allPageLink.get(i));
			CLIENT.waitForBackgroundJavaScriptStartingBefore(TIME_10S);
			CLIENT.waitForBackgroundJavaScript(TIME_5S);
			getSummaryPages(page, siteMeta);
		}
	}

	private List<String> getAllPageLink(int totalPage) {
		List<String> allPageLink = new ArrayList<>();
		for (int i = 2; i <= totalPage; i++) {
			String link = baseUrl + "/job-search-results/?pg=" + i;
			allPageLink.add(link);
		}
		return allPageLink;
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			Job job = new Job();
			List<HtmlElement> list = page.getBody().getElementsByAttribute("div", "class", "jobTitle");
			expectedJobCount += list.size();
			for (int i = 0; i < list.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				job.setTitle(list.get(i).getElementsByTagName("a").get(0).asText().trim());
				job.setName(job.getTitle());
				job.setUrl(baseUrl + list.get(i).getElementsByTagName("a").get(0).getAttribute("href"));
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (ElementNotFoundException e) {
			log.warn("Element not found : In page " + page.getBaseURI());
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		try {
			HtmlPage JobDetailPage = CLIENT.getPage(job.getUrl());
			CLIENT.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
			CLIENT.waitForBackgroundJavaScript(5 * 1000);

			String[] parts;
			parts = JobDetailPage.getBody().getOneHtmlElementByAttribute("span", "id", "gtm-jobdetail-id").asText().trim().split(":");
			job.setReferenceId(parts[1].trim());
			parts = JobDetailPage.getBody().getOneHtmlElementByAttribute("span", "id", "gtm-jobdetail-category").asText().trim().split(":");
			job.setCategory(parts[1].trim());
			parts = JobDetailPage.getBody().getOneHtmlElementByAttribute("span", "id", "gtm-jobdetail-city").asText().trim().split(":");
			job.setLocation(parts[1].trim());
			parts = JobDetailPage.getBody().getOneHtmlElementByAttribute("span", "id", "gtm-jobdetail-date").asText().trim().split(":");
			job.setPostedDate(parseDate(parts[1].trim(), DF,DF2));
			if (job.getPostedDate() == null) log.info(" failed to parse date value " + parts[1].trim() + " for job " + job.getUrl());
			job.setSpec(JobDetailPage.getBody().getOneHtmlElementByAttribute("div", "id", "gtm-jobdetail-desc").asText());
			job.setApplicationUrl(baseUrl + JobDetailPage.getBody().getOneHtmlElementByAttribute("a", "class", "button apply-btn").getAttribute("href"));
			return job;
		} catch (ElementNotFoundException e) {
			log.warn("Failed parse job details " + job.getUrl()+e);
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
		CLIENT.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
