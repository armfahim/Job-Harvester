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
 * Wendel Companies job site parser<br>
 * URL: https://wendelcompanies.com/about/careers/
 * 
 * @author Benajir Ullah
 * @since 2019-02-13
 */
@Slf4j
@Service
public class WendelCompanies extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.WENDEL;
	private static WebClient webClient = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.WENDEL));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 26);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);		
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws IOException,InterruptedException {
		try {
			List<HtmlElement> titleList = page.getByXPath("//dt[@class = 'accordion__title js-accordion-title flex spacebetween vmiddle']");
			List<HtmlElement> desList = page.getByXPath("//dd[@class = 'accordion__body faq__answer js-faq-answer']");
			expectedJobCount = titleList.size();
			for (int i = 0; i < titleList.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job();
				try {
					job.setTitle(titleList.get(i).getTextContent().trim().substring(0, titleList.get(i).getTextContent().trim().length() - 1));
					job.setName(titleList.get(i).getTextContent().trim().substring(0, titleList.get(i).getTextContent().trim().length() - 1));
					job.setSpec(desList.get(i).getTextContent().trim());
					job.setUrl(getBaseUrl() + getJobHash(job));
					saveJob(job, siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn("Failed to get job summary page" + e);
			throw e;
		}
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
