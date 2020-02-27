package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
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
 * Bank  NovaScotia Latin America job site parsing class.
 * URL: https://empleos.scotiabank.com/
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-07
 */
@Slf4j
@Service
public class BankNovaScotiaLatinAmerica extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANK_NOVA_SCOTIA_LATIN_AMERICA;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM. dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM. d, yyyy");

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 30);
		CLIENT = getFirefoxClient();
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);

		List<HtmlElement> jobLinks = page.getBody().getByXPath("//section[@class = 'job-list job-list-container']/ul/li/a");
		expectedJobCount = jobLinks.size();
		for(int i=0; i<jobLinks.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();			
			Job job = new Job(baseUrl + jobLinks.get(i).getAttribute("href"));
			try {
			saveJob(getJobDetail(job),siteMeta);
			}catch(Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		try {
			HtmlPage JobDetailPage = CLIENT.getPage(job.getUrl());
			CLIENT.waitForBackgroundJavaScript(TIME_5S);

			job.setTitle(JobDetailPage.getBody().getOneHtmlElementByAttribute("section", "class", "job-description").getElementsByTagName("h1").get(0).asText());
			job.setName(job.getTitle());
			job.setApplicationUrl(JobDetailPage.getBody().getOneHtmlElementByAttribute("a", "class", "button job-apply top").getAttribute("href"));
			job.setReferenceId(JobDetailPage.getBody().getOneHtmlElementByAttribute("span", "class", "job-id job-info").asText().replace("Job ID", "").trim());
			job.setPostedDate(parseDate(JobDetailPage.getBody().getOneHtmlElementByAttribute("span", "class", "job-date job-info").asText(), DF,DF2));
			job.setLocation(JobDetailPage.getBody().getOneHtmlElementByAttribute("span", "class", "job-loc job-info").asText().trim());
			job.setSpec(JobDetailPage.getBody().getOneHtmlElementByAttribute("div", "class", "ats-description").asText().trim());
			return job;
		} catch (ElementNotFoundException e) {
			log.warn("Failed parse job details " + job.getUrl()+e);
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
		CLIENT.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
