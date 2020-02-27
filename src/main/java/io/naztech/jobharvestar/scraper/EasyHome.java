package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * Easy Home job site scraper. <br>
 * URL: https://goeasy.talentnest.com/en?page=0
 * 
 * @author a.s.m. tarek
 * @since 2019-03-12
 */
@Slf4j
@Service
public class EasyHome extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.EASYHOME;
	private String baseUrl;
	private static WebClient client;
	private static final String TAILURL = "?page=";
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 32);
		HtmlPage page = client.getPage(site.getUrl());
		List<HtmlElement> el = page.getByXPath("//span[@class='hidden-phone']");
		int totalPage = Integer.parseInt(el.get(1).getLastElementChild().asText().trim());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			getSummaryPage(getBaseUrl() + TAILURL + i, site);
		}
	}

	private void getSummaryPage(String url, SiteMetaData site) throws PageScrapingInterruptedException {
		try {
			HtmlPage page = client.getPage(url);
			List<HtmlElement> el = page.getByXPath("//a[@class='job-link']");
			expectedJobCount += el.size();
			List<HtmlElement> location = page.getByXPath("//div[@class='posting-cell-content location']");
			for (int i = 0; i < el.size(); i++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + el.get(i).getAttribute("href"));
				job.setTitle(el.get(i).asText());
				job.setName(job.getTitle());
				String loc = location.get(i).asText().split("Location")[1].trim();
				if (loc.length() > 4)
					job.setLocation(loc);
				try {
					saveJob(getJobDetail(job), site);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse Summary page of " + url, e);
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element el = doc.selectFirst("div[class=posting-header]");
		String type = el.text().split("Employment Type:")[1].trim();
		if (type != null && ((type.equalsIgnoreCase("Full-Time")) || (type.equalsIgnoreCase("Part-Time")))) {
			job.setType(type);
		}
		el = doc.selectFirst("div[class=field rich-text-doc]");
		job.setSpec(el.text());
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
