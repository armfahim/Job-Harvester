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
 * Onex Corp Jobsite Parser<br>
 * URL: https://www.onexcompany.com/careers/
 * 
 * @author Mahmud Rana
 * @author fahim.reza
 * @since 2019-03-04
 */
@Service
@Slf4j
public class OnexCorp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ONEX_CORP;
	private int expectedJobCount;
	private Exception exception;
	private static final String ROW_EL_PATH = "//div[@class='vc_btn3-container vc_btn3-inline']/a";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		WebClient client = getFirefoxClient();
		try {
			System.out.println(siteMeta.getUrl());
			HtmlPage page = client.getPage(siteMeta.getUrl());
			client.waitForBackgroundJavaScript(5 * 1000);
			List<HtmlElement> rowList = page.getBody().getByXPath(ROW_EL_PATH);
			expectedJobCount = rowList.size();
			for (HtmlElement row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetail(row), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn(getSiteName() + " Fails to load page:" + siteMeta.getUrl(), e);
			throw e;
		}

	}

	private Job getJobDetail(HtmlElement row) throws IOException {
		Job job = new Job(row.getAttribute("href"));
		job.setTitle(row.getTextContent());
		job.setName(job.getTitle());
		job.setSpec(getTextFromPdf(row.getAttribute("href")));
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
