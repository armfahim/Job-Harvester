package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Zip Recruiter jobs site parse <br>
 * URL: https://www.ziprecruiter.com/careers
 * 
 * @author tanmoy.tushar
 * @since 2019-03-10
 */
@Service
@Slf4j
public class ZipRecruiter extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ZIPRECRUITER_;
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
		HtmlPage page = client.getPage(site.getUrl());
		List<Job> jobList = new ArrayList<>();
		DomElement nextE;
		do {
			if (isStopped()) throw new PageScrapingInterruptedException();
			jobList.addAll(getSummaryPages(site, page));
			nextE = page.getFirstByXPath("//a[@class='paginate_button next']");
			if (nextE != null)
				nextE.click();
			client.waitForBackgroundJavaScript(20 * 1000);
		} while (nextE != null);
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private List<Job> getSummaryPages(SiteMetaData site, HtmlPage page) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		try {
			List<HtmlElement> jobRowList = page.getBody().getByXPath("//table[@id='jobsTable']/tbody/tr");
			for (int i = 0; i < jobRowList.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				List<HtmlElement> jobDList = jobRowList.get(i).getElementsByTagName("td");
				HtmlElement jobUrl = jobDList.get(0).getOneHtmlElementByAttribute("a", "class", "block showVisited");
				Job job = new Job(jobUrl.getAttribute("href"));
				job.setTitle(jobDList.get(0).getTextContent());
				job.setName(job.getTitle());
				job.setLocation(jobDList.get(1).getTextContent());
				job.setCategory(jobDList.get(2).getTextContent());
				job.setPostedDate(parseAgoDates(jobDList.get(3).getTextContent()));
				jobList.add(job);
			}
		} catch (FailingHttpStatusCodeException e) {
			log.info(getSiteName() + " Exception Occured", e);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.getElementById("job_desc").text().trim());
		job.setType(doc.selectFirst("li[class=job_benefits_list_item employment_type]").text().trim());
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
