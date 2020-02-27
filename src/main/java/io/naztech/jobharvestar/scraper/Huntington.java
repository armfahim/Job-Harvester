package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

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
 * Huntington Banchshares jobs site parse <br>
 * URL: https://recruiting.adp.com/srccar/public/RTI.home?c=1047945&d=HuntingtonExternal
 * NEW URL: https://huntingtonbank.dejobs.org/jobs/
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-03-03
 */
@Service
@Slf4j
public class Huntington extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.HUNTINGTON_BANCSHARES;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount;
	private Exception exception;
	private WebClient client;
	private String baseUrl;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 33);
		HtmlPage page = client.getPage(site.getUrl());
		int totalClick = (getTotalJob(page) / 20);
		for (int i = 0; i < totalClick; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				HtmlElement moreJobs = page.getFirstByXPath("//a[@id='button_moreJobs']");
				if (moreJobs == null) break;
				moreJobs.click();
				Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_4S));
			} catch (Exception e) {
				log.warn("Failed to click or Unable to reach element at part " + (i + 1), e);
			}
		}
		try {
			browseJobList(page, site);
		} catch (Exception e) {
			log.error("Failed to parse job list", e);
		}
	}

	private void browseJobList(HtmlPage page, SiteMetaData site) throws PageScrapingInterruptedException {
		List<HtmlElement> jobList = page.getByXPath("//ul[@class='default_jobListing']/li/h4/a");
		for (HtmlElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + el.getAttribute("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(url).get();
		Element jobE = doc.selectFirst("span[itemprop=title]");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[itemprop=jobLocation]");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("meta[itemprop=datePosted]");
		if (jobE != null) job.setPostedDate(parseDate(jobE.attr("content").substring(0, 10), DF));
		jobE = doc.getElementById("direct_applyButtonBottom");
		if (jobE != null) job.setApplicationUrl(jobE.child(0).attr("href"));
		jobE = doc.getElementById("direct_jobDescriptionText");
		job.setSpec(jobE.text().trim());
		return job;
	}

	private int getTotalJob(HtmlPage page) {
		HtmlElement el = page.getFirstByXPath("//h3[@class='direct_highlightedText']");
		int totalJob = Integer.parseInt(el.asText().split(" ")[0].trim());
		expectedJobCount = totalJob;
		return totalJob;
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
