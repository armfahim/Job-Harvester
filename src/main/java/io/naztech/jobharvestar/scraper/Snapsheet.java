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
 * Snapsheet Jobsite Parser <br>
 * URL: https://snapsheet.applytojob.com/
 * 
 * @author iftekar.alam
 * @since 2019-04-01
 */
@Service
@Slf4j
public class Snapsheet extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SNAPSHEET;
	private String baseUrl;
	private static WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.SNAPSHEET));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getChromeClient();
		this.baseUrl = siteMeta.getUrl().substring(0, 32);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		getSummaryPages(page, siteMeta);

	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) {
		try {
			Thread.sleep(10000);
			List<HtmlElement> jobList = page.getByXPath("//ul[@class='list-group']/li/h4");
			expectedJobCount = jobList.size();
			for (HtmlElement li : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				HtmlElement link = li.getElementsByTagName("a").get(0);
				Job job = new Job();
				job.setTitle(link.asText());
				job.setName(job.getTitle());
				job.setUrl(link.getAttribute("href"));
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (InterruptedException | FailingHttpStatusCodeException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
		}

	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = webClient.getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(TIME_10S);
			HtmlElement spec = (HtmlElement) page.getByXPath("//div[@class='description']").get(0);
			job.setSpec(spec.asText());
			HtmlElement location = (HtmlElement) page.getByXPath("//ul[@class='list-inline job-attributes']/li").get(0);
			job.setLocation(location.asText());
			HtmlElement type = (HtmlElement) page.getByXPath("//ul[@class='list-inline job-attributes']/li").get(1);
			job.setType(type.asText());
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(" failed to parse detail page of " + job.getUrl(), e);
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
