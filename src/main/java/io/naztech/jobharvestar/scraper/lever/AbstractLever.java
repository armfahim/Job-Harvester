package io.naztech.jobharvestar.scraper.lever;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Abstract for Lever sites <br>
 * Convoy URL: https://jobs.lever.co/convoy<br>
 * Rappi URL: https://jobs.lever.co/rappi<br>
 * Affirm URL: https://jobs.lever.co/affirm<br>
 * Avant URL: https://jobs.lever.co/avant<br>
 * Brex URL: https://jobs.lever.co/brex <br>
 * Devoted Health URL: https://jobs.lever.co/devoted<br>
 * Ginkgo BioWorks URL: https://jobs.lever.co/ginkgobioworks<br>
 * Hike URL: https://jobs.lever.co/hike<br>
 * Lime URL: https://jobs.lever.co/limebike<br>
 * Zoox URL: https://jobs.lever.co/zoox
 * 
 * @author tohedul.islum
 * @since 2019-03-10
 */
public abstract class AbstractLever extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private String baseUrl;
	private WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		webClient = getChromeClient();
		SiteMetaData site = getSiteMetaData(getSiteName());
		setBaseUrl(site);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S * 6);
		List<HtmlElement> jobList = page.getByXPath("//div[@class='posting']");
		expectedJobCount = jobList.size();
		for (HtmlElement li : jobList) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			HtmlElement link = li.getElementsByTagName("a").get(1);
			Job job = new Job(link.getAttribute("href"));
			job.setTitle(link.getElementsByTagName("h5").get(0).asText());
			job.setName(link.getElementsByTagName("h5").get(0).asText());
			if (link.getElementsByTagName("span").size() > 1) {
				job.setLocation(link.getElementsByTagName("span").get(0).asText());
				job.setCategory(link.getElementsByTagName("span").get(1).asText());
			}
			if (link.getElementsByTagName("span").size() > 2) {
				job.setType(link.getElementsByTagName("span").get(2).asText());
			}
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to load job details page of " + job.getUrl(), e);
			}
		}
	}

	protected Job getJobDetail(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = webClient.getPage(job.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_4S);
		List<HtmlElement> detail = page.getByXPath("//div[@class='section-wrapper page-full-width']");
		job.setSpec(detail.get(0).asText());
		HtmlElement appUrl = (HtmlElement) page.getByXPath("//div[@class='postings-btn-wrapper']/a").get(0);
		job.setApplicationUrl(appUrl.getAttribute("href"));
		return job;
	}

	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 21);
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
