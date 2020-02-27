package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;
/**
 * NAME: Unicredit Germany and Austria Jobsite scraper
 * URL: https://www.unicreditbank.rs/rs/o-nama/hr/otvorene-pozicije.html
 * NOTE: Job detail page is in PDF format
 * 
 * @author Mahmud Rana
 * @since 2019-03-05
 */
@Service
@Slf4j
public class UnicreditSerbia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNICREDIT_SERBIA;
	private String baseUrl;
	private static final String ROW_EL_PATH = "//table[@class='no-border downloadlist table_nodecoration']/tbody/tr";
	private static final String TITLE_EL_PATH = ROW_EL_PATH+"/td[1]";
	private static final String PDF_EL_PATH = ROW_EL_PATH+"/td[4]/a";
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 28);		
		try {
			HtmlPage page = client.getPage(siteMeta.getUrl());
			client.waitForBackgroundJavaScript(TIME_10S);
			List<HtmlElement> rowList = page.getBody().getByXPath(ROW_EL_PATH);
			expectedJobCount = rowList.size();
			for (int i=0;i<rowList.size();i++) {
				Job job = new Job(getBaseUrl()+((HtmlElement)page.getBody().getByXPath(PDF_EL_PATH).get(i)).getAttribute("href"));
				job.setTitle(((HtmlElement)page.getBody().getByXPath(TITLE_EL_PATH).get(i)).getTextContent());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (Exception e) {
			log.warn(getSiteName()+" fails to load page. ", e);
			throw e;
		}finally {
			client.close();
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		job.setSpec(getTextFromPdf(job.getUrl()));
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
