package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
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
 * GitLab<br>
 * URL: https://about.gitlab.com/jobs/apply/
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-12
 */
@Slf4j
@Service
public class GitLab extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.GITLAB;
	private static WebClient webClient = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.GITLAB));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		HtmlPage page = getWeb().getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		try {
			Job job = new Job();
			List<HtmlElement> row = page.getByXPath("//div[@class = 'job-container']");
			expectedJobCount = row.size();
			for (int i = 0; i <= row.size() - 1; i++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				job.setTitle(row.get(i).getElementsByTagName("a").get(0).getTextContent());
				job.setName(row.get(i).getElementsByTagName("a").get(0).getTextContent());
				job.setUrl(this.baseUrl + row.get(i).getElementsByTagName("a").get(0).getAttribute("href"));
				job.setLocation(row.get(i).getElementsByTagName("h6").get(0).getTextContent());
				try {
				saveJob(getJobDetail(job), siteMeta);
				}catch(Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			
			log.warn("Exception on Job Summary Page" + e);
		}
	}

	private Job getJobDetail(Job job) {
		HtmlPage page;
		try {
			page = getWeb().getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(5000);
			HtmlElement desEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "content tile");
			job.setSpec(desEl.getTextContent());
			HtmlElement typeEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "header-content");
			job.setCategory(typeEl.getElementsByTagName("p").get(0).getTextContent());
			job.setApplicationUrl(typeEl.getElementsByTagName("a").get(0).getAttribute("href"));
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			
			log.warn("Exception on Job Detail Page" + e);
		}
		return null;
	}

	private WebClient getWeb() {
		if (webClient == null) {
			webClient = new WebClient(BrowserVersion.FIREFOX_52);
			webClient.waitForBackgroundJavaScript(20 * 1000);
			webClient.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
			webClient.getOptions().setTimeout(30 * 1000);
			webClient.getCookieManager().setCookiesEnabled(true);
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		}
		return webClient;
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