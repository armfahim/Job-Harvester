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
 * ReInsurance Group America<br>
 * URL: https://www.rgare.com/careers/search-careers/index.html
 * 
 * @author naym.hossain
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-02-13
 */
@Service
@Slf4j
public class ReInsuranceAmerica extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.REINSURANCE_GRP_AMERICA;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		client = getFirefoxClient();
		HtmlPage page = client.getPage(site.getUrl());
		client.waitForBackgroundJavaScript(TIME_10S);
		this.baseUrl = site.getUrl().substring(0, 45);
		int totalJob = getTotalJob(page);
		List<HtmlElement> jobList = page.getByXPath("//div[@class='table-scroll-wrap lumesse-jobs-list']/table/tbody/tr");
		browseJobList(jobList, site);
		for (int i = 2; i <= totalJob; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "index.html?page=" + i;
			try {
				page = client.getPage(url);
				client.waitForBackgroundJavaScript(TIME_5S);
				jobList = page.getByXPath("//div[@class='table-scroll-wrap lumesse-jobs-list']/table/tbody/tr");
				browseJobList(jobList, site);
			} catch (Exception e) {
				log.warn("Failed to parse list of " + url, e);
			}
		}
	}

	private int getTotalJob(HtmlPage page) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlElement totalJobS = page.getFirstByXPath("//span[@class='badge']");
		String totalJob = totalJobS.asText().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
	}

	private void browseJobList(List<HtmlElement> jobList, SiteMetaData site) throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		for (HtmlElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl()+ el.getElementsByTagName("th").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
			job.setTitle(el.getElementsByTagName("th").get(0).getElementsByTagName("a").get(0).asText().trim());
			job.setName(job.getTitle());
			job.setReferenceId(el.getElementsByTagName("td").get(0).asText().trim());
			String location = el.getElementsByTagName("td").get(2).asText().trim();
			if (location != "" || location != null) job.setLocation(location); 
			else job.setLocation(el.getElementsByTagName("td").get(1).asText().trim());
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(job.getUrl());
		client.waitForBackgroundJavaScript(5000);
		HtmlElement spec = page.getFirstByXPath("//div[@id='lumesseJobDetailWidget']");
		job.setSpec(spec.asText().trim());
		HtmlElement applyUrl = page.getFirstByXPath("//a[@class = 'btn btn-primary btn-lg pull-right']");
		job.setApplicationUrl(applyUrl.getAttribute("href"));
		return job;
	}

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
