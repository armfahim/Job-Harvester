package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * UNIHTC job site scraper.<br>
 * URL: https://usa.united-imaging.com/careers/
 * 
 * @author Kayumuzzaman Robin
 * @author tanmoy.tushar
 * @author jannatul.maowa
 * @since 2019-03-18
 */
@Slf4j
@Service
public class UnitedImagingHealth extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNITED_IMAGING_HEALTHCARE;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		final WebWindow topLevelWindow = client.getTopLevelWindows().get(0);
		topLevelWindow.setInnerWidth(300);
		topLevelWindow.setInnerHeight(600);
		HtmlPage page = client.getPage(siteMeta.getUrl());
		List<HtmlElement> jobLinks = page.getByXPath("//div[@class='l-career__item _flex']/div/a");
		expectedJobCount = jobLinks.size();
		for (HtmlElement htmlElement : jobLinks) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(htmlElement.getAttribute("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				log.info("Failed to parse job details " + job.getUrl(), e);
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h2");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("li[class=posInfo]>div[class=posInfo__Value]");
			if (jobE != null)
				job.setLocation(jobE.text());
			jobE = doc.selectFirst("li[class=posInfo posInfo--department]>div[class=posInfo__Value]");
			if (jobE != null)
				job.setCategory(jobE.text());
			jobE = doc.selectFirst("li[class=posInfo posInfo--employmentType]>div[class=posInfo__Value]");
			if (jobE != null)
				job.setType(jobE.text());
			jobE = doc.selectFirst("div[class=ResAts__card-content]");
			job.setSpec(jobE.text());
		} catch (NullPointerException e) {
			return getJobDetailSecondPattern(job);
		}
		return job;
	}

	private Job getJobDetailSecondPattern(Job job) throws IOException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h1");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("div[class=s-position__subtitle]");
			if (jobE != null)
				job.setLocation(jobE.text());
			jobE = doc.selectFirst("div[class=_wysiwyg]");
			job.setSpec(jobE.text());
		} catch (NullPointerException e) {
			log.warn("Failed to parsed job Details", e);
			return null;
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
