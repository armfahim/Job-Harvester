package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
 * Earnin Job site parse handling iframe<br>
 * URL: https://www.earnin.com/careers
 * 
 * @author jannatul.maowa
 * @since 2019-04-01
 */
@Service
@Slf4j
public class Earnin extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.EARNIN;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		baseUrl = siteMeta.getUrl().substring(0, 22);
		Elements jobCategoryList = doc.select("div[class=vacancies-department-wrapper]");
		expectedJobCount = doc.select("div[class=views-row vacancies-item]").size();
		for (int i = 0; i < jobCategoryList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Elements jobE = jobCategoryList.get(i).select("div[class=vacancy-apply]>a");
			for (int j = 0; j < jobE.size(); j++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + jobE.get(j).attr("href"));
				job.setCategory(jobCategoryList.get(i).selectFirst("div[class=vacancy-department]").text().trim());
				try {
					saveJob(jobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		}
	}

	private Job jobDetails(Job job) {
		try (WebClient client = getFirefoxClient()) {
			HtmlPage page = client.getPage(job.getUrl());
			HtmlElement body = page.getBody();
			HtmlElement iframeLink = body.getFirstByXPath("//iframe[@id='grnhse_iframe']");
			page = client.getPage(iframeLink.getAttribute("src"));
			body = page.getBody();
			String title = body.getOneHtmlElementByAttribute("h1", "class", "app-title").getTextContent().trim();
			job.setTitle(title);
			job.setName(job.getTitle());
			job.setLocation(body.getOneHtmlElementByAttribute("div", "class", "location").getTextContent().trim());
			job.setSpec(body.getOneHtmlElementByAttribute("div", "id", "content").getTextContent().trim());

		} catch (IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}