package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
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
 * Etoro Jobsite Parser<br> 
 * https://www.etoro.com/about/careers/#main_jobs_content
 * 
 * @author Rahat Ahmad
 * @since 2019-04-02
 */
@Slf4j
@Service
public class Etoro extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ETORO;
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
		List<HtmlElement> jobListE = page.getByXPath("//div[@class='job']");
		expectedJobCount = jobListE.size();
		getSummaryPage(jobListE, page, siteMeta);
	}

	private void getSummaryPage(List<HtmlElement> jobListE, HtmlPage page, SiteMetaData siteMeta){
		try {
			for (int i = 0; i < jobListE.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setTitle(jobListE.get(i).getElementsByTagName("div").get(0).getTextContent());
				job.setName(job.getTitle());
				job.setCategory(jobListE.get(i).getElementsByTagName("div").get(1).getTextContent());
				page = jobListE.get(i).getElementsByTagName("a").get(0).click();
				Thread.sleep(TIME_1S * 2);
				List<HtmlElement> jobDetailE = page.getByXPath("//div[@class='job open']");
				try {
					saveJob(getJobDetails(jobDetailE, job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (ElementNotFoundException | IOException | InterruptedException e) {
			log.warn("Failed to parse job summary of "+ siteMeta.getUrl(), e);
		}
	}

	private Job getJobDetails(List<HtmlElement> jobDetailE, Job job) {
		try {
			job.setSpec(jobDetailE.get(jobDetailE.size() - 1).getOneHtmlElementByAttribute("div", "class", "job_desc")
					.getTextContent());
			job.setLocation(jobDetailE.get(jobDetailE.size() - 1)
					.getOneHtmlElementByAttribute("div", "class", "job_location col-sm-4").getTextContent()
					.replace("location", "").trim());
			job.setApplicationUrl(jobDetailE.get(jobDetailE.size() - 1)
					.getOneHtmlElementByAttribute("a", "class", "send_resume").getAttribute("href"));
		} catch (ElementNotFoundException e) {
			log.warn("Failed to parse job details of "+ job.getTitle(), e);
		}
		job.setUrl(getJobHash(job));
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
