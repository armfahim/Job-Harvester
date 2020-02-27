package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.TimeoutException;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
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
 * Singapore Exchange Job site parser<br>
 * URL: https://career.sgx.com/viewjobs.php
 * 
 * @author Mahmud Rana
 * @since 2019-02-11
 */
@Service
@Slf4j
public class SingaporeExchange extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SINGAPORE_EXCHANGE;
	private static final String JOB_ROW_EL_PATH = "//table[@id='summaryJobTbl']/tbody/tr/td/a";
	private static final String JOB_BRIEF_EL_PATH = "//table[@id='briefJobInfo']/tbody/tr";
	private static final String COLLAPSE_EL_PATH = "//div[@class='CollapsiblePanelContent']";
	private static final String APPLY_EL_PATH = "//div[@id='buttonApplyJobs']/div/a";

	private static WebClient client = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 22);
		try {
			HtmlPage page = getClient().getPage(site.getUrl());
			client.waitForBackgroundJavaScript(8 * 1000);
			List<HtmlElement> rowList = page.getBody().getByXPath(JOB_ROW_EL_PATH);
			expectedJobCount = rowList.size();
			HtmlPage detailP;
			for (HtmlElement row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Thread.sleep(RandomUtils.nextInt(1000, 2000));
				detailP = client.getPage(getBaseUrl() + row.getAttribute("href"));
				client.waitForBackgroundJavaScript(8 * 1000);
				try {
					Job job = getJobDetail(detailP);
					saveJob(job, site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (TimeoutException e) {
			log.warn(getSiteName() + ": Fails to load page. " + e.getMessage());
			throw e;
		} finally {
			client.close();
		}

	}

	private Job getJobDetail(HtmlPage detailP) {
		Job job = new Job();
		job.setUrl(detailP.getUrl().toString());
		HtmlElement el = (HtmlElement) detailP.getBody().getByXPath(JOB_BRIEF_EL_PATH).get(0);
		job.setTitle(el.getTextContent().split(":")[1].trim());
		job.setName(job.getTitle());
		el = (HtmlElement) detailP.getBody().getByXPath(JOB_BRIEF_EL_PATH).get(2);
		job.setCategory(el.getTextContent().split(":")[1].trim());
		el = (HtmlElement) detailP.getBody().getByXPath(JOB_BRIEF_EL_PATH).get(2);
		job.setReferenceId(el.getTextContent().split(":")[1].trim());
		el = (HtmlElement) detailP.getBody().getByXPath(COLLAPSE_EL_PATH).get(1);
		job.setSpec(el.getTextContent());
		el = (HtmlElement) detailP.getBody().getByXPath(COLLAPSE_EL_PATH).get(2);
		job.setPrerequisite(el.getTextContent());
		el = (HtmlElement) detailP.getBody().getByXPath(APPLY_EL_PATH).get(1);
		job.setApplicationUrl(getBaseUrl() + el.getAttribute("href"));
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}

	private WebClient getClient() {
		if (client == null) {
			client = new WebClient(BrowserVersion.FIREFOX_52);
			client.getOptions().setTimeout(45 * 1000);
			client.getOptions().setUseInsecureSSL(true);
			client.getCookieManager().setCookiesEnabled(true);
			client.setJavaScriptTimeout(30 * 1000);
		}
		return client;
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
