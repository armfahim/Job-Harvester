package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * Insurance Australia Group jobs site parser. <br>
 * URL: https://careers.iag.com.au/search/?q=&sortColumn=referencedate&sortDirection=desc
 * 
 * @author kamrul.islam
 * @author tanmoy.tushar
 * @since 2019-03-18
 */
@Slf4j
@Service
public class InsuranceAustraliaGrp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INSURANCE_AUSTRALIA_GROUP;
	private static WebClient client = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 26);
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(TIME_10S);
		int totalJob = getTotalJob(page);
		for (int i = 0; i < totalJob; i += 25) {
			String url = siteMeta.getUrl() + "&startrow=" + i;
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}		
		}
	}

	private void browseJobList(String url, SiteMetaData siteMeta) throws PageScrapingInterruptedException, FailingHttpStatusCodeException, IOException {
		HtmlPage page = client.getPage(url);
		List<HtmlElement> list = page.getBody().getElementsByAttribute("tr", "class", "data-row clickable");
		for (HtmlElement it2 : list) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			HtmlElement el = it2.getElementsByTagName("td").get(0).getElementsByTagName("span").get(0).getElementsByTagName("a").get(0);
			Job job = new Job(getBaseUrl() + el.getAttribute("href"));
			job.setTitle(el.asText());
			job.setName(job.getTitle());
			job.setLocation(it2.getElementsByTagName("td").get(1).asText());
			job.setType(it2.getElementsByTagName("td").get(2).asText());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job deatils of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		HtmlPage page = client.getPage(job.getUrl());
		job.setSpec(page.getBody().getElementsByAttribute("div", "class", "col-xs-12 fontalign-left").get(1).asText().trim());
		return job;
	}
	
	private int getTotalJob(HtmlPage page) {
		HtmlElement el = page.getBody().getOneHtmlElementByAttribute("span", "class", "paginationLabel");
		String totalJob = el.asText().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getExpectedJob();
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
