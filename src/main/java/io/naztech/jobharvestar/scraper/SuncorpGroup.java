package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Suncorp Group site parse handling iframe<br>
 * URL: https://careers.pageuppeople.com/346/cdw/en/listing
 * 
 * @author tanmoy.tushar
 * @since 2019-04-04
 */
@Service
@Slf4j
public class SuncorpGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SUNCORP_GROUP;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final String EXT_STRING_1 = "/346/cdw/en/listing/?page=";
	private static final String EXT_STRING_2 = "&page-items=20";
	private static WebClient webClient = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		webClient = getFirefoxClient();
		HtmlPage page = webClient.getPage(site.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S);
		baseUrl = site.getUrl().substring(0, 32);
		int totalPage = getTotalPage(page);
		for (int i = 1; i <= totalPage + 1; i++) {
			String url = getBaseUrl() + EXT_STRING_1 + i + EXT_STRING_2;
			try {
				getSummaryPage(site, url);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void getSummaryPage(SiteMetaData site, String url) throws IOException {
		HtmlPage page = webClient.getPage(url);
		List<HtmlElement> jobList = page.getByXPath("//h4[@class='card-title']/a");
	    for (HtmlElement el : jobList) {
	    	Job job = new Job(getBaseUrl() + el.getAttribute("href"));
	    	job.setTitle(el.asText().trim());
	    	job.setName(job.getTitle());
	    	try {
				saveJob(getJobDetails(job), site);
			}catch (NullPointerException e) {
				saveJob(getJobDetails1(job), site);
			}catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("span[class=open-date]>time");
		job.setPostedDate(parseDate(jobE.text().trim(), DF,DF1));
		jobE = doc.selectFirst("span[class=close-date]>time");
		job.setDeadline(parseDate(jobE.text().trim(), DF,DF1));
		jobE = doc.selectFirst("span[class=location]");
		job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("a[class=apply-link button]");
		job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("div[class=card px-4 mb-3]");
		job.setSpec(jobE.text().trim());
		return job;
	}
	
	private Job getJobDetails1(Job job) throws IOException {
		Document doc;
		try {
			doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Element jobE = doc.selectFirst("span[class=job-externalJobNo]");
			job.setReferenceId(jobE.text().trim());
			jobE = doc.selectFirst("span[class=open-date]>time");
			job.setPostedDate(parseDate(jobE.text().trim(), DF,DF1));
			jobE = doc.selectFirst("span[class=close-date]>time");
			job.setDeadline(parseDate(jobE.text().trim(), DF,DF1));
			jobE = doc.selectFirst("span[class=location]");
			job.setLocation(jobE.text().trim());
			jobE = doc.selectFirst("span[class=categories]");
			job.setCategory(jobE.text().trim());
			jobE = doc.selectFirst("a[class=apply-link button]");
			job.setApplicationUrl(jobE.attr("href"));
			jobE = doc.selectFirst("div[id=job-details]");
			job.setSpec(jobE.text().trim());
			return job;
		} catch (NullPointerException e) {
			exception = e;
			log.warn("Failed to parse job detail of " + job.getUrl(), e);
			return null;
		}
	}
	
	private int getTotalPage(HtmlPage page) {
		HtmlElement el = (HtmlElement) page.getByXPath("//span[@class='count']").get(1);
		String totalJob = el.asText(); 
		expectedJobCount = Integer.parseInt(totalJob) + 20;
		return getPageCount(totalJob, 20);
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
