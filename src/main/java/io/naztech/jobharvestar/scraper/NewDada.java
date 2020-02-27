package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

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
 * <a href="https://www.imdada.cn/job">
 * New Dada Job Site Parser </a><br> 
 * 
 * @author Fahim Reza
 * @since 2019-03-20
 */
@Service
@Slf4j
public class NewDada extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NEW_DADA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String baseUrl;
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		this.baseUrl = siteMeta.getUrl().substring(0, 21);
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(9000);
		List<HtmlElement> jobLink = page.getBody().getByXPath("//div[@class='job-list']/a");
		expectedJobCount = jobLink.size();
		for (HtmlElement url : jobLink) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(baseUrl + url.getAttribute("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	public Job getJobDetails(Job job) throws InterruptedException, MalformedURLException {
		try {
			if (isStopped()) throw new PageScrapingInterruptedException();
			HtmlPage page = client.getPage(job.getUrl());
			client.waitForBackgroundJavaScript(10 * 1000);
			HtmlElement title = page.getBody().getFirstByXPath("//h1[@class='job-detail-title pull-left']");
			HtmlElement location = page.getBody().getFirstByXPath("//span[@class='job-detail-location-name']");
			HtmlElement postedDate = (HtmlElement) page.getBody().getByXPath("//div[@class='col-lg-4']").get(2);
			List<HtmlElement> spec = page.getBody().getByXPath("//div[@class='job-detail-content']/div[2]");
			List<HtmlElement> pre = page.getBody().getByXPath("//div[@class='job-detail-content']/div[3]");
			String date = postedDate.getElementsByTagName("span").get(1).asText();
			job.setTitle(title.asText());
			job.setName(job.getTitle());
			job.setLocation(location.asText());
			job.setPostedDate(parseDate(date, DF));
			job.setSpec(spec.get(0).asText());
			job.setPrerequisite(pre.get(0).asText());
		} catch (FailingHttpStatusCodeException | NoSuchElementException | IOException e) {
			log.warn(SITE + "Failed to parse job details " + job.getUrl(), e);
		}
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
