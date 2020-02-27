package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

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
 * STX Entertainment jobs site parse <br>
 * URL:
 * https://www.paycomonline.net/v4/ats/web.php/jobs?clientkey=2DD1AB85BA64404231B79480499596D1&jpt=#
 * 
 * @author tanmoy.tushar
 * @since 2019-03-31
 */
@Service
@Slf4j
public class StxEntertainment extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.STX_ENTERTAINMENT;
	private static WebClient client;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		client = getFirefoxClient();
		HtmlPage page = client.getPage(site.getUrl());
		client.waitForBackgroundJavaScript(TIME_10S);
		this.baseUrl = site.getUrl().substring(0, 28);
		List<HtmlElement> jobList = page.getByXPath("//a[@class='JobListing__container']");
		expectedJobCount = jobList.size();
		if(expectedJobCount == 0) log.warn("No job avaialable ");
		for (HtmlElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.getAttribute("href"));
			job.setTitle(el.getElementsByTagName("div").get(0).getElementsByTagName("span").get(0).asText().trim());
			job.setName(job.getTitle());
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("div[name=description]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("div[name=qualifications]");
		job.setPrerequisite(jobE.text().trim());
		jobE = doc.selectFirst("div[name=Job Location]");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("div[name=Position Type]");
		if (jobE != null) job.setType(jobE.text().trim());
		jobE = doc.selectFirst("div[name=Job Category]");
		if (jobE != null) job.setCategory(jobE.text().trim());
		return job;
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
