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
 * NuComGroup Jobsite Parser<br>
 * URL: https://www.prosiebensat1-jobs.com/stellenangebote.html?reset_search=0&search_mode=job_filter_advanced&filter%5Bvolltext%5D=&filter%5Bclient_id%5D%5B%5D=92
 *
 * @author Rahat Ahmad
 * @since 2019-03-14
 */
@Slf4j
@Service
public class NuComGroup extends AbstractScraper implements Scrapper {
	
	private static final String SITE = ShortName.NUCOM_GROUP;
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
		List<Job> jobList = getSummaryPage(page);
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			try {
				saveJob(getJobDetails(job, page), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}
	
	private List<Job> getSummaryPage(HtmlPage page){
		List<HtmlElement> jobLinksE = page.getByXPath("//div[@class='middle']/a");
		List<Job> jobList = new ArrayList<>();
		for (HtmlElement htmlElement : jobLinksE) {
			Job job = new Job();
			job.setUrl(htmlElement.getAttribute("href"));
			job.setTitle(htmlElement.getTextContent());
			job.setName(job.getTitle());
			jobList.add(job);
		}
		return jobList;
	}
	
	private Job getJobDetails(Job job , HtmlPage page) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		page = client.getPage(job.getUrl());
		try {
			HtmlElement spec = page.getFirstByXPath("//div[@class='emp_nr_left']");
			job.setSpec(spec.asText());
			HtmlElement category = (HtmlElement) page.getByXPath("//div[@class='emp_box_content']").get(0);
			job.setCategory(category.asText());
			HtmlElement location = (HtmlElement) page.getByXPath("//div[@class='emp_box_content']").get(1);
			job.setLocation(location.asText());
		}catch(ElementNotFoundException e) {
			log.warn("Element not found for url: "+job.getUrl(),e);
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
