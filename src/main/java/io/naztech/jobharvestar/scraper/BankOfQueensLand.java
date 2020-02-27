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
 * Bank of Queensland Job site parser<br>
 * URL: https://boq-openhire.silkroad.com/epostings/index.cfm?fuseaction=app.allpositions&company_id=16496&version=1
 * 
 * @author Mahmud Rana
 * @since 2019-02-11
 */
@Service
@Slf4j
public class BankOfQueensLand extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANK_OF_QUEENSLAND;
	private static final String JOB_ROW_EL_PATH = "//a[@class='cssAllJobListPositionHref']";
	private static final String JOB_TITLE_EL_ID = "jobTitleDiv";
	private static final String JOB_REF_EL_ID = "jobCodeDiv";
	private static final String JOB_LOC_EL_ID = "jobPositionLocationDiv";
	private static final String JOB_TYPE_EL_ID = "translatedJobPostingTypeDiv";

	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}
	
	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		try {
			HtmlPage page = client.getPage(site.getUrl());
			client.waitForBackgroundJavaScript(15*1000);
			List<HtmlElement> rowList = page.getBody().getByXPath(JOB_ROW_EL_PATH);
			expectedJobCount = rowList.size();
			for (HtmlElement row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Thread.sleep(3345, 4565);
				HtmlPage dPage =row.click(); 
				client.waitForBackgroundJavaScript(15*1000);
				try {
					Job job = getJobDetail(dPage);
					saveJob(job, site);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch(FailingHttpStatusCodeException e) {
			log.warn(getSiteName()+": ");
			throw e;
		} finally {
			client.close();
		}
	}

	private Job getJobDetail(HtmlPage page) {
		Job job = new Job();
		job.setUrl(page.getUrl().toString());
		HtmlElement el = (HtmlElement) page.getElementById(JOB_TITLE_EL_ID);
		job.setTitle(el.getTextContent().trim());
		job.setName(job.getTitle());
		el = (HtmlElement) page.getElementById(JOB_REF_EL_ID);
		job.setReferenceId(el.getTextContent().trim());
		el = (HtmlElement) page.getElementById(JOB_LOC_EL_ID);
		job.setLocation(el.getTextContent().trim());
		el = (HtmlElement) page.getElementById(JOB_TYPE_EL_ID);
		job.setType(el.getTextContent().trim());
		el = (HtmlElement) page.getElementById("jobDesciptionDiv");
		job.setSpec(el.getTextContent().trim());
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
