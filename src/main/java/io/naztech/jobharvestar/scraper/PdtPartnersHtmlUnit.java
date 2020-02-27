package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * PDT Partners (Process Driven Trading) Jobsite Parser<br>
 * URL: https://jobs.pdtpartners.com/
 * 
 * @author Rahat Ahmad
 * @since 2019-03-07
 */
@Service
public class PdtPartnersHtmlUnit extends AbstractScraper implements Scrapper {
	
	private static final String SITE = ShortName.PDT_PARTNERS_PROCESS_DRIVEN_TRADING;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		client.getOptions().setTimeout(30*1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30*1000);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(TIME_5S);
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		List<String> links = getSummaryPage(page);
		expectedJobCount = links.size();
		for(String jobLink : links) {
			try {
				saveJob(getJobDetails(jobLink , page), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
			Thread.sleep(TIME_5S);
		}
	}
	
	public List<String> getSummaryPage(HtmlPage page){
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='opening']/a");
		List<String> links = new ArrayList<String>();
		for(HtmlElement el:jobLinksE) {
			links.add(el.getAttribute("href"));
		}
		return links;
	} 
	
	public Job getJobDetails(String url , HtmlPage page) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		Job job = new Job();
		job.setUrl(url);
		page = client.getPage(url);
		client.waitForBackgroundJavaScript(TIME_5S);
		List<FrameWindow> frames1 = page.getFrames();
		page = (HtmlPage) frames1.get(0).getEnclosedPage();
		HtmlElement title = (HtmlElement) page.getByXPath("//div[@id='header']/h1").get(0);
		job.setTitle(title.asText());
		HtmlElement location = (HtmlElement) page.getByXPath("//div[@class='location']").get(0);
		job.setLocation(location.asText());
		job.setSpec(page.getElementById("content").asText());
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
