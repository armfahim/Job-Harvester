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
 * Xiaohongshu<br>
 * URL: https://xiaohongshu.quip.com/about/jobs#job-listings
 * 
 * @author rafayet.hossain
 * @since 2019-03-21
 */
@Service
@Slf4j
public class Xiaohongshu extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.XIAOHONGSHU;
	private String baseUrl;
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl();
		browseJobList(site);
	}

	private void browseJobList(SiteMetaData site) throws PageScrapingInterruptedException, FailingHttpStatusCodeException, IOException {
		HtmlPage page;
		page = client.getPage(site.getUrl());
		List<HtmlElement> rowList = page.getByXPath("//div[@class='openings']/ul/li/a");
		expectedJobCount = rowList.size();
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(baseUrl + rowList.get(i).getAttribute("href"));
			job.setTitle(rowList.get(i).getTextContent());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetails(job, i), site);
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job, int position) {
		try {
			HtmlPage detailsPage = client.getPage(job.getUrl());
			HtmlElement description = (HtmlElement) detailsPage
					.getByXPath("//div[@class='modal-content col-sm-12 col-md-10 col-md-offset-1']").get(position);
			job.setSpec(description.getTextContent());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed parse job details of" + job.getUrl(), e);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
