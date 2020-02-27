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
 * NN Group<br>
 * URL: https://www.deshaw.com/careers/choose-your-path
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-3-4
 */
@Slf4j
@Service
public class DeShawCo extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DE_SHAW_N_CO;
	private static WebClient webClient = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.DE_SHAW_N_CO));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		HtmlPage page = getWeb().getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		try {
			List<HtmlElement> row = page.getByXPath("//div[@class = 'description-wrapper']");
			List<HtmlElement> row1 = page.getByXPath("//div[@class = 'information']");
			expectedJobCount = row.size();
			for (int i = 0; i <= row.size() - 1; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setUrl(this.baseUrl + row.get(i).getElementsByTagName("a").get(0).getAttribute("href"));
				job.setReferenceId(
						row.get(i).getElementsByTagName("a").get(0).getAttribute("href").split("careers/")[1].trim());
				job.setLocation(row1.get(i).getElementsByTagName("span").get(0).getTextContent());
				job.setCategory(row1.get(i).getElementsByTagName("p").get(0).getTextContent());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.error("Exception on Job Summary Page" + e);
		}
	}

	private Job getJobDetail(Job job) {
		HtmlPage page;
		try {
			page = getWeb().getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(5000);
			HtmlElement nameEl = page.getBody().getOneHtmlElementByAttribute("h1", "class",
					"PageIntro-title sectionTitle");
			job.setTitle(nameEl.getTextContent());
			job.setName(nameEl.getTextContent());
			HtmlElement descEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "page-container");
			job.setSpec(descEl.getTextContent());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Exception on Job Detail Page" + job.getUrl(), e);
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