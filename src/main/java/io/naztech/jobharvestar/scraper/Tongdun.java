package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
 * Tongdun Technology<br>
 * Url: http://jobs.tongdun.cn/req/social/?page=1
 * 
 * @author rafayet.hossain
 * @since 2019-03-18
 */
@Service
@Slf4j
public class Tongdun extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TONGDUN_TECHNOLOGY;
	private String baseUrl;
	private static WebClient client;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount = 0;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.TONGDUN_TECHNOLOGY));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 22);
		HtmlPage page = client.getPage(site.getUrl());
		List<HtmlElement> nextE = page.getBody().getByXPath("//ul[@class='pagination real-show']/li/a");
		int i = 1;
		while (true) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			page = client.getPage(getBaseUrl() + "/req/social/?page=" + i);
			if (nextE.get(nextE.size() - 1).getAttribute("href").trim().contains("#")) {
				break;
			} else {
				browseJobList(site, page);
				nextE = page.getBody().getByXPath("//ul[@class='pagination real-show']/li/a");
				i++;
			}
		}
	}

	private void browseJobList(SiteMetaData site, HtmlPage page) {
		try {
			List<HtmlElement> jobList1 = page.getByXPath("//td[@class='col-md-3']/a");
			expectedJobCount += jobList1.size();
			for (HtmlElement jobElement : jobList1) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setTitle(jobElement.getTextContent());
				job.setName(job.getTitle());
				job.setUrl(baseUrl + jobElement.getAttribute("href"));
				try {
					saveJob(getJobDetail(job), site);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | PageScrapingInterruptedException e) {
			log.warn("Failed parse job details", e);
		}
	}

	private Job getJobDetail(Job job) {
		HtmlPage detailsPage;
		try {
			detailsPage = client.getPage(job.getUrl());
			HtmlElement location = (HtmlElement) detailsPage.getByXPath("//span[@class='bold-txt']").get(1);
			HtmlElement type = (HtmlElement) detailsPage.getByXPath("//span[@class='bold-txt']").get(4);
			HtmlElement applicationUrl = (HtmlElement) detailsPage
					.getByXPath("//button[@class='btn btn-default btn-hover-sty']").get(0);
			HtmlElement postDate = (HtmlElement) detailsPage.getByXPath("//span[@class='bold-txt']").get(2);
			HtmlElement description = (HtmlElement) detailsPage
					.getByXPath("//div[@class='col-md-12 common-font-other']").get(0);
			HtmlElement qualification = (HtmlElement) detailsPage
					.getByXPath("//div[@class='col-md-12 common-font-other']").get(1);
			job.setLocation(location.getTextContent());
			job.setType(type.getTextContent());
			job.setApplicationUrl(baseUrl + applicationUrl.getAttribute("onclick").split("'")[1]);
			job.setPostedDate(parseDate(postDate.getTextContent(), DF));
			job.setSpec(description.getTextContent());
			job.setPrerequisite(qualification.getTextContent());
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
