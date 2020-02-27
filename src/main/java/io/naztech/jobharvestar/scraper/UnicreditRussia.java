package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Unicredit Russia.<br>
 * URL: https://hh.ru/search/vacancy?text=unicredit&area=1
 * 
 * @author naym.hossain
 * @author assaduzzaman.sohan
 * @author tanmoy.tushar
 * @since 2019-02-04
 */
@Slf4j
@Service
public class UnicreditRussia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNICREDIT_RUSSIA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String HEADURL = "/vacancy?L_is_autosearch=false&area=1&clusters=true&enable_snippets=true&text=unicredit&page=";

	private String baseUrl;
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		client.getOptions().setJavaScriptEnabled(false);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 20);
		int totalPage = getTotalPage(site.getUrl());
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + HEADURL + i;
			try {
				browseJobList(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	public void browseJobList(String url, SiteMetaData site) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("a[class=bloko-link HH-LinkModifier]");
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = el.attr("href");
			try {
				saveJob(getJobDetail(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + jobUrl, e);
			}
		}
	}

	private Job getJobDetail(String jobUrl) throws IOException {
		Job job = new Job(jobUrl);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1[itemprop=title]");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("a[data-qa=vacancy-response-link-top]");
		if (jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		jobE = doc.selectFirst("span[data-qa=vacancy-view-raw-address]");
		if (jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("span[data-qa=vacancy-experience]");
		if (jobE != null) job.setPrerequisite(jobE.text());
		jobE = doc.selectFirst("meta[itemprop=datePosted]");
		if (jobE != null) job.setPostedDate(parseDate(jobE.attr("content").substring(0, 10), DF));
		jobE = doc.selectFirst("meta[itemprop=employmentType]");
		if (jobE != null) job.setType(jobE.attr("content"));
		jobE = doc.selectFirst("div[class=wrap_hh_content]");
		if(jobE == null) jobE = doc.selectFirst("div[itemprop=description]"); 
		job.setSpec(jobE.text());		
		return job;
	}

	private int getTotalPage(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Element el = doc.selectFirst("h1[data-qa=page-title]");
		String totalJob = el.text().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
