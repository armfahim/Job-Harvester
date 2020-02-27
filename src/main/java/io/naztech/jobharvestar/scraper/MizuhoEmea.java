package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
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
 * MUFG Emea Jobsite Parser. <br>
 * URL: https://cbcareers.mizuho-emea.com/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=0
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-02-17
 */
@Service
@Slf4j
public class MizuhoEmea extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MIZUHO_FINANCIAL_GROUP_EMEA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String ROW_LIST = "//span[@class='jobTitle hidden-phone']/a";
	private String baseUrl;
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 33);
		HtmlPage page = client.getPage(site.getUrl());
		client.waitForBackgroundJavaScript(TIME_10S);
		HtmlElement el = page.getFirstByXPath("//span[@class='paginationLabel']/b[2]");
		int totalJob = getTotalJob(el);
		List<HtmlElement> jobList = page.getByXPath(ROW_LIST);
		browseJobList(jobList, site);
		for (int i = 10; i < totalJob; i += 10) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=" + i;
			try {
				page = client.getPage(url);
				client.waitForBackgroundJavaScript(TIME_10S);
				jobList = page.getByXPath(ROW_LIST);
				browseJobList(jobList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(List<HtmlElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (HtmlElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.getAttribute("href"));
			job.setTitle(el.asText().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = client.getPage(job.getUrl());
		HtmlElement jobE = page.getFirstByXPath("//time[@itemprop='datePosted']");
		if (jobE != null) job.setPostedDate(parseDate(jobE.getAttribute("datetime"), DF));
		jobE = page.getFirstByXPath("//span[@itemprop='jobLocation']");
		if (jobE != null) job.setLocation(jobE.asText().trim());
		jobE = page.getFirstByXPath("//div[@class='applylink pull-right']/a");
		if (jobE != null) job.setApplicationUrl(jobE.getAttribute("href"));
		jobE = page.getFirstByXPath("//span[@itemprop='description']");
		job.setSpec(jobE.asText().trim());
		return job;
	}

	private int getTotalJob(HtmlElement el) {
		return expectedJobCount = Integer.parseInt(el.asText().trim());
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
