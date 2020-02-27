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
 * Citizen Bank Job site Parser<br>
 * URL: https://jobs.citizensbank.com/search-jobs
 * 
 * @author Mahmud Rana
 * @author iftekar.alam
 * @since 2019-02-10
 */
@Service
@Slf4j
public class CitizensBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CITIZENS_FINANCIAL_GROUP;
	private static final String ROW_ANCHOR_PATH = "//section[@id='search-results-list']/ul/li/a";
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM. dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static WebClient client;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		client = getFirefoxClient();
		this.baseUrl = site.getUrl().substring(0, 29);
		try {
			HtmlPage page = client.getPage(site.getUrl());
			client.waitForBackgroundJavaScript(TIME_10S + TIME_5S);
			HtmlElement tpage = page.getFirstByXPath("//span[@class = 'pagination-total-pages']");
			int totalPage = Integer.parseInt(tpage.asText().split("of")[1].trim());
			expectedJobCount=totalPage * 15;
			List<HtmlElement> rowList = page.getBody().getByXPath(ROW_ANCHOR_PATH);
			for (int i = 0; i < totalPage; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				browseJobList(rowList, site);
				if (i == totalPage - 1)	break;
				HtmlElement el = page.getFirstByXPath("//a[@class='next']");
				if (el != null) {
					page = el.click();
					Thread.sleep(RandomUtils.nextInt(TIME_1S * 3, TIME_5S));
				}
				client.waitForBackgroundJavaScript(TIME_5S);
				rowList = page.getBody().getByXPath(ROW_ANCHOR_PATH);
			}
		} catch (Exception e) {
			log.error("Failed to parse job list, Scraper quiting by exception");
			throw e;
		} finally {
			client.close();
		}
	}

	private void browseJobList(List<HtmlElement> rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (HtmlElement row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + row.getAttribute("href"));
			job.setTitle(row.getElementsByTagName("h2").get(0).asText());
			job.setName(job.getTitle());
			job.setLocation(row.getElementsByTagName("span").get(0).asText());
			job.setPostedDate(parseDate(row.getElementsByTagName("span").get(1).asText(), DF1,DF2,DF3));
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element spec = doc.selectFirst("div[class=ats-description]");
		job.setSpec(spec.text());
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