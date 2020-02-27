package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
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
 * Corp Ezetap jobsite parser<br>
 * URL: https://corp.ezetap.com/careers/
 * 
 * @author Arifur Rahman
 * @since 2019-03-31
 */
@Service
@Slf4j
public class Ezetap extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.EZETAP;
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getChromeClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta)
			throws IOException, InterruptedException, FailingHttpStatusCodeException {
		HtmlPage page = client.getPage(siteMeta.getUrl());
		getSummaryPages(page, siteMeta);
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) throws PageScrapingInterruptedException {
		try {
			List<HtmlElement> jobTitleList = page.getBody().getByXPath("//div[@class='job-role']/h4");
			List<HtmlElement> jobTitleList1 = page.getBody().getByXPath("//div[@class='acc-desc']");
			List<HtmlElement> locationList = page.getBody().getByXPath("//div[@class='job-details']/p[2]");
			expectedJobCount = jobTitleList.size();
			for (int j = 0; j < jobTitleList.size(); j++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setTitle(jobTitleList.get(j).getTextContent());
				String loc[] = locationList.get(j).getTextContent().split("–");
				job.setLocation(loc[1].trim());
				try {
					saveJob(getJobDetails(jobTitleList1.get(j), job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (ElementNotFoundException e) {
			log.warn("Failed to parse summaray of " + SITE, e);
		}
	}

	private Job getJobDetails(HtmlElement spec, Job job) {
		job.setSpec(spec.getTextContent());
		job.setUrl(getJobHash(job));
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
