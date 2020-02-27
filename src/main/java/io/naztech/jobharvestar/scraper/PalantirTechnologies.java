package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Palantir Technologies Job Site Parser<br>
 * URL: https://www.palantir.com/careers/
 * 
 * @author Rahat Ahmad
 * @since 2019-03-10
 */
@Slf4j
@Service
public class PalantirTechnologies extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PALANTIR_TECHNOLOGIES;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(TIME_4S*2);
		List<String> jobUrl = getSummaryPage(page);
		expectedJobCount = jobUrl.size();
		for (String url : jobUrl) {
			try {
				saveJob(getJobDetails(page,url), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}
	
	public List<String> getSummaryPage(HtmlPage page){
		List<HtmlElement> jobListE = page.getByXPath("//div[@class = 'positions-list']/div/ul/li/ul/li");
		List<String> jobList = new ArrayList<>();
		for(HtmlElement el : jobListE) {
			List<HtmlElement> links = el.getElementsByTagName("a");
			jobList.add(links.get(0).getAttribute("href"));
		}
		return jobList;
	} 
	
	public Job getJobDetails(HtmlPage page , String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		Job job = new Job();
		page = client.getPage(url);
		client.waitForBackgroundJavaScript(TIME_5S);
		HtmlElement title = page.getFirstByXPath("//div[@class='posting-headline']/h2");
		try {
			job.setUrl(url);
			job.setTitle(title.getTextContent());
			HtmlElement spec = (HtmlElement) page.getByXPath("//div[@class='section page-centered']").get(0);
			job.setSpec(spec.getTextContent());
			job.setName(job.getTitle());
			HtmlElement location = page.getFirstByXPath("//div[@class='posting-categories']/div[1]");
			job.setLocation(location.getTextContent());
			HtmlElement preReq = (HtmlElement) page.getByXPath("//div[@class='section page-centered']").get(1);
			job.setPrerequisite(preReq.getTextContent());
		}catch(ElementNotFoundException e) {
			log.warn("Data not found" , e);
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
