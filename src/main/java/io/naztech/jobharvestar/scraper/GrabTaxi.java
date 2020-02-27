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
 * GrabTaxi <br> 
 * URL: https://grab.careers/jobs/
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-12
 */
@Slf4j
@Service
public class GrabTaxi extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.GRABTAXI;
	private static WebClient webClient = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.GRABTAXI));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getChromeClient();
		this.baseUrl = siteMeta.getUrl().substring(0, 21);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		try {
			Job job = new Job();
			List<HtmlElement> rows = page.getByXPath("//tbody[@class='content jobs-list']/tr");
			expectedJobCount = rows.size();
			for (HtmlElement row : rows) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				job.setTitle(row.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getTextContent());
				job.setName(row.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getTextContent());
				job.setUrl(this.baseUrl
						+ row.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
				job.setLocation(row.getElementsByTagName("td").get(1).getTextContent());
				job.setReferenceId(job.getUrl().split("id=")[1]);
				try {
				saveJob(getJobDetail(job), siteMeta);
				}catch(Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn(SITE + "Exception on Job Summary Page" + e);
		}
	}
	

	private Job getJobDetail(Job job) {
		HtmlPage page;
		try {
			page = webClient.getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(5000);
			HtmlElement desEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "job_desc");
			job.setSpec(desEl.getTextContent());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(SITE + "Exception on Job Detail Page" + e);
		}
		return null;
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