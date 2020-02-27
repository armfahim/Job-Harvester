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
 * Bank New York URL:
 * URL: https://chc.tbe.taleo.net/chc01/ats/careers/v2/searchResults?org=COMMERZBANK&cws=92
 * 
 * @author tohedul.islum
 * @since 2019-02-03
 */
@Service
@Slf4j
public class CommerzBankNewYork extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.COMMERZBANK_NEW_YORK;
	private String baseUrl;
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	@Override
	public void scrapJobs() throws Exception{
		client = getChromeClient();
		startSiteScrapping(getSiteMetaData(ShortName.COMMERZBANK_NEW_YORK));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		getSummaryPages(siteMeta.getUrl(), siteMeta);

	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		try {
			HtmlPage page = client.getPage(url);
			List<HtmlElement> Ulist = page.getByXPath(
					"//div[@class='oracletaleocwsv2-accordion oracletaleocwsv2-accordion-expandable clearfix']");
			expectedJobCount = Ulist.size();
			for (HtmlElement row : Ulist) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				HtmlElement link = row.getElementsByTagName("a").get(0);
				Job job = new Job(link.getAttribute("href"));
				job.setTitle(link.asText());
				job.setName(job.getTitle());
				List<HtmlElement> lc = row.getByXPath("//div[@class='oracletaleocwsv2-accordion-head-info']/div");
				job.setCategory(lc.get(0).asText());
				job.setLocation(lc.get(1).asText());
				try {
				saveJob(getJobDetail(job), siteMeta);
				}catch(Exception e) {
					exception = e;
				}
			}

		} catch (IOException e) {
			log.warn(SITE + " failed to connect site", e);
			throw e;
		}
	}

	private Job getJobDetail(Job job) {

		try {
			HtmlPage page = client.getPage(job.getUrl());
			HtmlElement spec = (HtmlElement) page.getByXPath("//div[@class='col-xs-12 col-sm-12 col-md-8']").get(0);
			job.setSpec(spec.asText());
			HtmlElement appUrl = (HtmlElement) page.getByXPath("//div[@class='col-xs-12 col-sm-12 col-md-4']").get(0);
			job.setApplicationUrl(appUrl.getElementsByTagName("a").get(0).getAttribute("href"));
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(SITE + " failed to parse job", e);
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
