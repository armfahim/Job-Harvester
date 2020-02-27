package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * SEI INVESTMENTS CO job site parser. <br>
 * URL: https://seic.com/careers/job-search
 * 
 * @author tohedul.islum
 * @author jannatul.maowa
 * @since 2019-03-04
 */
@Service
@Slf4j
public class SeiInvestments extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SEI_INVESTMENTS_CO;
	private String baseUrl;
	private WebClient webClient = null;
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM dd yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM d yyyy");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.SEI_INVESTMENTS_CO));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl();
		HtmlPage pageM = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S * 2);
		expectedJobCount = getTotalJob(pageM);
		getSummaryPages(pageM, siteMeta);
		List<HtmlElement> nextPageList = pageM.getBody().getByXPath("//ul[@class='pagination au-target']/li/a");
		for (int i = 2; i < nextPageList.size() - 1; i++) {
			pageM = webClient.getPage(nextPageList.get(i).getAttribute("href"));
			webClient.waitForBackgroundJavaScript(TIME_10S * 2);
			getSummaryPages(pageM, siteMeta);
		}
	}

	private void getSummaryPages(HtmlPage pageM, SiteMetaData siteMeta) {
		List<HtmlElement> jobList = pageM.getBody().getByXPath("//div[@class='information']/a");
		for (int i = 0; i < jobList.size(); i++) {
			Job job = new Job(jobList.get(i).getAttribute("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage(job.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S * 2);
		HtmlElement jobEl = page.getBody().getOneHtmlElementByAttribute("h1", "class", "job-title");
		job.setTitle(jobEl.getTextContent().trim());
		job.setName(job.getTitle());
		jobEl = page.getBody().getOneHtmlElementByAttribute("span", "class", "job-details-jobId au-target");
		job.setReferenceId(jobEl.getTextContent().trim());
		jobEl = page.getBody().getOneHtmlElementByAttribute("span", "class", "job-details-postedDate au-target");
		String str = jobEl.getTextContent().split("posted")[1].trim();
		job.setPostedDate(parseDate(str.replaceAll("(?<=\\d)(st|nd|rd|th)", ""), DF1, DF2));
		jobEl = page.getBody().getOneHtmlElementByAttribute("span", "data-ph-id", "ph-page-element-page11-RfiS5F");
		job.setLocation(jobEl.getTextContent().split("location")[1].trim());
		jobEl = page.getBody().getOneHtmlElementByAttribute("span", "data-ph-id", "ph-page-element-page11-268IYb");
		job.setType(jobEl.getTextContent().split("type")[1].trim());
		jobEl = page.getBody().getOneHtmlElementByAttribute("span", "data-ph-id", "ph-page-element-page11-0BrETF");
		job.setCategory(jobEl.getTextContent().split("category")[1].trim());
		jobEl = page.getFirstByXPath("//a[@title='Apply Now']");
		job.setApplicationUrl(jobEl.getAttribute("href"));
		jobEl = page.getFirstByXPath("//section[@class='job-description']");
		job.setSpec(jobEl.getTextContent().trim());
		return job;
	}

	private int getTotalJob(HtmlPage page) {
		HtmlElement el = page.getFirstByXPath("//span[@class='result-count']");
		return Integer.parseInt(el.asText().trim());
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
