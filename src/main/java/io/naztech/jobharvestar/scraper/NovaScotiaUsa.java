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
 * Bank Nova Scotia Usa and Emea.<br>
 * URL: https://usa.jobs.scotiabank.com/search-jobs
 * 
 * @author naym.hossain
 * @since 2019-01-30
 */
@Slf4j
@Service
public class NovaScotiaUsa extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANK_NOVA_SCOTIA_USA_AND_EMEA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("M/d/yyyy");
	private String baseUrl;
	private WebClient webClient;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.BANK_NOVA_SCOTIA_USA_AND_EMEA));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 31);
		HtmlPage page = webClient.getPage(site.getUrl());
		getSummaryPages(page, site);
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData site) {
		try {
			List<HtmlElement> rows = page.getByXPath("//section[@id = 'search-results-list']/ul/li");
			expectedJobCount = rows.size();
			for (HtmlElement row : rows) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				String jobUrl = getBaseUrl() + row.getElementsByTagName("a").get(0).getAttribute("href");
				Job job = new Job(jobUrl);
				job.setName(row.getElementsByTagName("h2").get(0).getTextContent().trim());
				job.setTitle(job.getName());
				job.setLocation(row.getElementsByTagName("span").get(0).getTextContent().trim());
				job.setPostedDate(parseDate(row.getElementsByTagName("span").get(1).getTextContent().trim(), DF));
				if (job.getPostedDate() == null)
					log.warn(" failed to parse date value "
							+ row.getElementsByTagName("span").get(1).getTextContent().trim() + " for job "
							+ job.getUrl());
				try {
					saveJob(getJobDetail(job), site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | InterruptedException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
		}
	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = webClient.getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(70 * 1000);
			List<HtmlElement> elJobId = page.getByXPath("//span[@class = 'job-id job-info']");
			job.setReferenceId(elJobId.get(0).getTextContent().split(" ")[2].trim());
			List<HtmlElement> des = page.getByXPath("//div[@class = 'ats-description']");
			job.setSpec(des.get(0).getTextContent().trim());
			HtmlElement applyButton = page.getBody().getOneHtmlElementByAttribute("a", "class", "button job-apply top");
			job.setApplicationUrl(applyButton.getAttribute("href"));
		} catch (IOException | FailingHttpStatusCodeException e) {
			log.warn(" Failed parse job details of " + job.getUrl(), e);
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
