package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

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
 * Principal Financial Group HongKong. <br>
 * URL: https://www.principal.com.hk/en/content/careers
 * 
 * @author Benajir Ullah
 * @author tanmoy.tushar
 * @since 2019-02-13
 */
@Slf4j
@Service
public class PrincipalFinancialHK extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PRINCIPAL_FINANCIAL_GRP_HK;
	private static WebClient webClient;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.PRINCIPAL_FINANCIAL_GRP_HK));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 27);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S);
		try {
			getSummaryPage(page, siteMeta);			
		} catch (Exception e) {
			log.warn("Failed to parse job list page", e);
		}
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		List<HtmlElement> titleList = page.getByXPath("//span[@class = 'list-title']");
		List<HtmlElement> desList = page.getByXPath("//div[@class = 'list-details']");
		expectedJobCount = titleList.size() / 2;
		for (int i = 0; i < titleList.size(); i += 2) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job();
			try {
				job.setTitle(titleList.get(i).asText().trim());
				job.setName(job.getTitle());
				job.setPrerequisite(desList.get(i + 1).asText().trim());
				job.setSpec(desList.get(i).asText().trim());
				job.setUrl(getBaseUrl() + getJobHash(job));
				saveJob(job, siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getTitle(), e);
			}
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
