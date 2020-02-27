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
 * Poste Italiane job site parser. <br>
 * URL: https://erecruiting.poste.it/posizioniAperte.php
 * 
 * @author Benajir Ullah
 * @since 2019-02-14
 */
@Slf4j
@Service
public class PosteItaliane extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.POSTE_ITALIANE;
	private static WebClient webClient = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.POSTE_ITALIANE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 29);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		List<HtmlElement> titleList = page.getByXPath("//tr[@class = 'Result']");
		expectedJobCount = titleList.size();
		for (HtmlElement row : titleList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + row.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
			job.setReferenceId(row.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getTextContent().trim());
			job.setTitle(row.getElementsByTagName("td").get(1).getElementsByTagName("a").get(0).getTextContent().trim());
			job.setName(row.getElementsByTagName("td").get(1).getElementsByTagName("a").get(0).getTextContent().trim());
			job.setCategory(row.getElementsByTagName("td").get(2).getTextContent().trim());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page;
		page = webClient.getPage(job.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		HtmlElement desEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "descrizione");
		HtmlElement reqEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "requisiti");
		job.setSpec(desEl.getTextContent());
		job.setPrerequisite(reqEl.getTextContent());
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
