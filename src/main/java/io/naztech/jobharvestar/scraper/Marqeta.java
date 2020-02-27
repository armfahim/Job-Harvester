package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * Marqeta job site parser <br>
 * URL: https://www.marqeta.com/company/careers/all-jobs
 * 
 * @author rafayet.hossain
 * @author iftekar.alam
 * @since 2019-04-02
 */
@Slf4j
@Service
public class Marqeta extends AbstractScraper implements Scrapper {
	private String baseUrl;
	private static WebClient webClient = null;
	private int expectedJobCount = 0;
	private Exception exception;
	private static final String SITE = ShortName.MARQETA;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getChromeClient();
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		getSummaryPages(page, siteMeta);
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		try {
			Thread.sleep(10000);
			List<HtmlElement> jobList = page.getByXPath("//table[@id='jobs_table']/tbody/tr/td/a");
			expectedJobCount = jobList.size() / 3;
			for (int i = 0; i < jobList.size(); i += 3) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + jobList.get(i).getAttribute("href"));
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (InterruptedException | FailingHttpStatusCodeException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
		}
	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobF = doc.selectFirst("div[id=job_description]>h1");
			job.setTitle(jobF.text());
			job.setName(job.getTitle());
			Element jobG = doc.select("div[class=job_sidebar--content]").get(0);
			job.setCategory(jobG.text());
			Element jobH = doc.select("div[class=job_sidebar--content]").get(1);
			job.setLocation(jobH.text());
			Element jobE = doc.selectFirst("div[id=job_description]");
			job.setSpec(jobE.text());
			return job;
		} catch (IOException | NullPointerException e) {
			log.warn(" Failed parse job details of " + job.getUrl(), e);
		}
		return null;
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
