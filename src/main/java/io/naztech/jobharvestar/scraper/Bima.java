package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
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
 * Bima job site scrapper job url: http://www.bimamobile.com/why-bima/vacancies/
 * 
 * @author ariful.islam
 * @since 2019-04-01
 */
@Slf4j
@Service
public class Bima extends AbstractScraper implements Scrapper {
	private static final String site = ShortName.BIMA;
	private String baseUrl;
	private static final String ROW_EL_PATH = "//div[@class='Content__Widget--size-narrow']";
	private static final String JOB_LIST_TITLE = ROW_EL_PATH + "/h2/strong";
	private static final String JOB_LIST_DESC = ROW_EL_PATH + "/p/a";
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getChromeClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 25);
		HtmlPage page = client.getPage(siteMeta.getUrl());
		getSummaryPages(page, siteMeta);

	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		try {
			Thread.sleep(10000);
			List<HtmlElement> jobList = page.getBody().getByXPath(JOB_LIST_TITLE);
			List<HtmlElement> linkList = page.getBody().getByXPath(JOB_LIST_DESC);

			int i = 0;
			expectedJobCount = linkList.size();
			for (HtmlElement jobLink : linkList) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + jobLink.getAttribute("href"));
				job.setTitle(jobList.get(i).getTextContent().split(":")[1].trim());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
				i++;
			}

		} catch (FailingHttpStatusCodeException e) {
			log.warn("Failed to load JobSite", e);
			throw e;
		}

	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		try {
			job.setSpec(getTextFromPdf(job.getUrl()));

		} catch (FailingHttpStatusCodeException e) {
			log.error("Failed to load page: ", e);
		}
		return job;
	}

	@Override
	public String getSiteName() {
		return site;
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