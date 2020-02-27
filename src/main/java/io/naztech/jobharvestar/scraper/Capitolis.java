package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Capitolis job site parsing class. <br>
 * URL: https://www.capitolis.com/careers/
 * 
 * @author marjana.akter
 * @since 2019-04-01
 */
@Service
@Slf4j
public class Capitolis extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CAPITOLIS;
	private static WebClient client = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws PageScrapingInterruptedException,IOException {
		try {
			HtmlPage page = client.getPage(site.getUrl());
			List<HtmlElement> jSummary = page.getByXPath("/html/body/div[5]/div[2]/div[1]/div/div[1]/section/div/div");
			List<HtmlElement> jDetails = page.getByXPath("//div[@class='col-sm-8 left job_desc_holder']");
			List<HtmlElement> jLocation = page
					.getByXPath("//div[@class='col-sm-3 col-sm-offset-1 left job_desc_holder']");
			expectedJobCount = jSummary.size();
			for (int i = 0; i < jSummary.size(); i++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job();
				try {
					job.setTitle((((DomNode) jSummary.get(i).getByXPath("div/div[@class='job_heading']").get(0))
							.getTextContent()));
					job.setName(job.getTitle());
					job.setLocation(((HtmlElement) jLocation.get(i).getFirstByXPath("div[@class='job_content']"))
							.getTextContent().trim());
					job.setSpec(((HtmlElement) jDetails.get(i).getFirstByXPath("div[@class='job_content']"))
							.getTextContent().trim());
					job.setUrl(getJobHash(job));
					saveJob(job, site);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn(" Failed parse job details of " + e);
			throw e;
		}
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
