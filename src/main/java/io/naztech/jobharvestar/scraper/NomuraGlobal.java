package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
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
 * Nomura Global job site parser<br>
 * URL: https://nomuracareers.resourcesolutions.com/gold/cbjbaaa/jobsearch/jobresults.cfm
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-18
 */
@Slf4j
@Service
public class NomuraGlobal extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NOMURA_HOLDINGS_GLOBAL;
	private static WebClient webClient = null;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("d MMM yyyy");
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 67);
		HtmlPage page = getWeb().getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_5S);
		List<HtmlElement> viewAllEl = page.getBody().getElementsByAttribute("span","class","RJobTitle");
		for (HtmlElement el : viewAllEl) {
			if ("View All".equalsIgnoreCase(el.getTextContent())) {
				page = el.click();
				break;
			}
		}
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		try {
			Job job = new Job();
			List<HtmlElement> row = page.getByXPath("//div[@class = 'JSResForm']/div[@style='text-align:left;']");
			expectedJobCount = row.size();
			for (int i = 0; i <= row.size() - 1; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				job.setTitle(row.get(i).getElementsByTagName("div").get(0).getElementsByTagName("a").get(0)
						.getTextContent().split("-")[1].trim());
				job.setName(job.getTitle());
				job.setReferenceId(row.get(i).getElementsByTagName("div").get(0).getElementsByTagName("a").get(0)
						.getTextContent().split("-")[0].trim());
				job.setUrl(getBaseUrl() + row.get(i).getElementsByTagName("div").get(0).getElementsByTagName("a").get(0)
						.getAttribute("href"));
				job.setCategory(row.get(i).getElementsByTagName("div").get(1).getTextContent().split(":")[1].trim());
				job.setPostedDate(parseDate(row.get(i).getElementsByTagName("div").get(2).getTextContent().split(":")[1]
						.trim().substring(2), DF,DF1));
				if (job.getPostedDate() == null)
					log.warn("Failed to parse date value " + row.get(i).getElementsByTagName("ul").get(0)
							.getElementsByTagName("li").get(1).getTextContent() + " for job " + job.getUrl());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn("Failed to parse summary page of " + getSiteName(), e);
		}
	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = getWeb().getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(TIME_5S);
			try {
				job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "WordSection1").getTextContent().trim());
				return job;
			} catch (ElementNotFoundException e) {
				job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "JDetailFormDetail").getTextContent().trim());
				return job;
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse job details of "+ job.getUrl(), e);
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