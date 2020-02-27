package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
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
 * Toro Jobsite Parser<br>
 * URL: https://toro-openhire.silkroad.com/epostings/index.cfm?fuseaction=app.allpositions&company_id=16279&version=1
 * 
 * @author Fahim Reza
 * @since 2019-03-31
 */
@Service
@Slf4j
public class Toro extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TORO;
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
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			this.baseUrl = siteMeta.getUrl().substring(0, 34);
			HtmlPage page = client.getPage(siteMeta.getUrl());
			client.waitForBackgroundJavaScript(TIME_1M);
			List<HtmlElement> jobLink = page.getByXPath("//div[@class='cssAllJobListPosition']/a");
			expectedJobCount = jobLink.size();
			for (HtmlElement url : jobLink) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(baseUrl + url.getAttribute("href"));
				job.setTitle(url.getTextContent());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetails(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn(SITE + " failed to connect site", e);
			throw e;
		}
	}

	public Job getJobDetails(Job job) throws InterruptedException, MalformedURLException {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			client.waitForBackgroundJavaScript(10 * 1000);
			HtmlElement location = page.getBody().getFirstByXPath("//dd[@id='jobPositionLocationDiv']");
			HtmlElement type = page.getBody().getFirstByXPath("//dd[@id='translatedJobPostingTypeDiv']");
			HtmlElement referenceId = page.getBody().getFirstByXPath("//dd[@id='jobCodeDiv']");
			HtmlElement spec = page.getBody().getFirstByXPath("//dd[@id='jobDesciptionDiv']");
			job.setLocation(location.asText());
			job.setType(type.asText());
			job.setReferenceId(referenceId.asText());
			job.setSpec(spec.asText());
		} catch (FailingHttpStatusCodeException e) {
			log.warn(SITE + " failed to connect site", e);
		} catch (NoSuchElementException e) {
			log.warn(SITE + " Failed to parse job details of " + job.getUrl(), e);
		} catch (IOException e) {
			log.warn(SITE + " failed to connect site", e);
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
