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
 * CANDRIAM URL:
 * https://www.candriam.com/en/professional/job-page/JobsDetailsPage/
 * 
 * @author Md. Sanowar Ali
 * @author iftekar.alam
 * @since 2019-02-28
 */
@Service
@Slf4j
public class Candriam extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CANDRIAM_INVESTORS_GROUP;
	private static final String TAILURL = "/en/professional/job-page/JobsDetailsPage/";
	private String baseUrl;
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(ShortName.CANDRIAM_INVESTORS_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		client = getFirefoxClient();
		String url = getBaseUrl() + TAILURL;
		try {
			getSummaryPages(url, siteMeta);
		} catch (Exception e) {
			log.warn("Failed to parse list of " + url, e);
		}
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws IOException, PageScrapingInterruptedException {
		HtmlPage page = client.getPage(url);
		client.waitForBackgroundJavaScript(TIME_4S);
		List<HtmlElement> list = page.getByXPath("//div[@class='row generatedelement']");
		expectedJobCount = list.size();
		for (HtmlElement row : list) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			HtmlElement link = row.getElementsByTagName("a").get(0);
			Job job = new Job(getBaseUrl() + link.getAttribute("href"));
			job.setCategory(row.getElementsByTagName("div").get(0).asText().trim());
			job.setTitle(row.getElementsByTagName("div").get(1).asText());
			job.setName(job.getTitle());
			job.setLocation(row.getElementsByTagName("div").get(2).asText().replace("Apply Now", "").trim());
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	public Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element spec = doc.selectFirst("div[class=ad]");
		job.setSpec(spec.text());
		job.setPrerequisite(spec.select("ul").text());
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
