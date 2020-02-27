package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * VISA job site parser <br>
 * URL: https://usa.visa.com/careers.html#1
 * 
 * @author tanmoy.tushar
 * @since 2019-04-07
 */
@Service
@Slf4j
public class Visa extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.VISA_USA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static WebClient client;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		List<Job> jobList = new ArrayList<>();
		HtmlPage page = client.getPage(site.getUrl());
		List<HtmlElement> rowList = page.getByXPath("//tbody[@id='unsortedtable']/tr");
		jobList.addAll(getSummaryPages(site, rowList));
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private List<Job> getSummaryPages(SiteMetaData site, List<HtmlElement> rowList)
			throws PageScrapingInterruptedException {
		List<Job> jobL = new ArrayList<>();
		String id = null;
		String url = "https://www.smartrecruiters.com/Visa/";
		String addUrl = "?oga=true";
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			List<HtmlElement> jobInfoL = rowList.get(i).getElementsByTagName("td");
			id = jobInfoL.get(0).getElementsByTagName("a").get(0).getAttribute("data-jobid");
			Job job = new Job(url + id + addUrl);
			job.setTitle(jobInfoL.get(0).asText());
			job.setName(job.getTitle());
			job.setCategory(jobInfoL.get(1).asText());
			job.setType(jobInfoL.get(2).asText());
			job.setLocation(jobInfoL.get(3).asText());
			job.setPostedDate(parseDate(jobInfoL.get(4).asText(), DF));
			jobL.add(job);
		}
		return jobL;
	}

	private Job getJobDetails(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("div[itemprop=description]");
			if(jobE != null)
				job.setSpec(jobE.text());
			jobE = doc.selectFirst("section[id=st-qualifications]");
			if(jobE != null)
				job.setPrerequisite(jobE.text());
			return job;
		} catch (IOException e) {
			log.warn(getSiteName() + " Failed parse jobs details of " +job.getUrl());
		}
		return null;
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
