package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Canva jobs site parse <br>
 * URL: https://www.canva.com/careers/jobs/
 * 
 * @author tanmoy.tushar
 * @since 2019-03-12
 */
@Service
@Slf4j
public class Canva extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CANVA;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		List<Job> jobList = new ArrayList<>();
		jobList.addAll(getSummaryPages(site));
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
			saveJob(getJobDetails(job), site);
			}catch(Exception e) {
				exception = e;
			}
		}
	}

	private List<Job> getSummaryPages(SiteMetaData site) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		try {
			Document doc = Jsoup.connect(site.getUrl()).get();
			Elements rowList = doc.select("a[class=jobListItem__link]");
     		for (int i = 0; i < rowList.size(); i++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job(rowList.get(i).attr("href"));
				jobList.add(job);
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info(getSiteName() + " Exception Occured", e);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.select("h1[class=jobPosting__heading title--extraLarge]").first();
			job.setTitle(jobE.text());
			job.setName(jobE.text());
			jobE = doc.select("div[class=jobPosting__discipline title--micro]").first();
			job.setCategory(jobE.text().split("tea")[0].trim());
			jobE = doc.select("div[class=jobPosting__location title--large]").first();
			job.setLocation(jobE.text().split("–")[0]);
			job.setType(jobE.text().split("–")[1].trim());
			job.setApplicationUrl(job.getUrl());
			jobE = doc.select("div[class=jobPosting__details]").first();
			job.setSpec(jobE.text());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info(getSiteName() + " Failed parse job details of " + job.getUrl(), e);
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
