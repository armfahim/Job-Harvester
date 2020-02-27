package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
 * Supreme jobs site parse <br>
 * URL: https://www.supremegroup.com/careers/career-opportunities/
 * 
 * @author tanmoy.tushar
 * @since 2019-03-28
 */
@Service
@Slf4j
public class Supreme extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SUPREME;
	private static final DateTimeFormatter DF_1 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private static final DateTimeFormatter DF_2 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		List<Job> jobList = new ArrayList<>();
		this.baseUrl = site.getUrl().substring(0, 28);
		Elements rowList = doc.select(
				"div[style=padding:3px 15px 3px 15px;overflow:hidden;text-align:center;background-color:#F9CE28;]>a");
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

	private List<Job> getSummaryPages(SiteMetaData site, Elements rowList) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		for (Element row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + row.attr("href"));
			jobList.add(job);
		}
		return jobList;
	}

	private Job getJobDetails(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Elements jobInfoL = doc.select("div[style=padding:0px;margin:0px;]>div>div");
			job.setTitle(jobInfoL.get(0).selectFirst("div[class=left]").text());
			job.setName(job.getTitle());
			job.setReferenceId(jobInfoL.get(1).text().split(":")[1].trim());
			job.setPostedDate(parseDate(jobInfoL.get(2).text().split(":")[1].trim(), DF_1, DF_2));
			job.setCategory(jobInfoL.get(5).text().split(" ")[1].trim());
			job.setType(jobInfoL.get(6).text().split("Type")[1].trim());
			job.setLocation(jobInfoL.get(7).text().split("tion")[1].trim());
			job.setSpec(jobInfoL.get(8).text());
			job.setApplicationUrl(job.getUrl());
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
		return baseUrl;
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
