package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;

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
 * Suntrust Banks job site parser.<br>
 * URL: https://jobs.suntrust.com/ListJobs/All
 * 
 * @author naym.hossain
 * @author iftekar.alam
 * @since 2019-02-10
 */
@Slf4j
@Service
public class SuntrustBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SUNTRUST_BANKS;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("M/dd/yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("d/M/yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 26);
		Document doc = Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		for (int i = 1; i <= getTotalPage(doc); i++) {
			String url=site.getUrl()+i;
			try {
				getSummaryPage(url,site);
			} catch (Exception e) {
				exception=e;
				log.warn("Failed to parse details of "+url,e);
			}
		}
	}
	
	private void getSummaryPage(String url,SiteMetaData site) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("td[class=coldisplayjobid]>a");
		for (Element el : rowList) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(getBaseUrl()+el.attr("href"));
			try {
				saveJob(getJobDetail(job), site);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+job.getUrl(),e);
			}
		}
	}
	
	private int getTotalPage(Document doc) {
		String totalJob=doc.select("span[class=pager_counts]").get(0).text().split("of")[1].trim();
		expectedJobCount=Integer.parseInt(totalJob);
		return getPageCount(totalJob, 30);
	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.select("div[class=jobdescription-row description]>div").get(1).text().trim());
		Element jobE = doc.select("div[class=jobdescription-row location]>div").get(1);
		if(jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.select("div[class=jobdescription-row shorttextfield2]>div").get(1);
		if(jobE != null) job.setType(jobE.text().trim());
		jobE = doc.select("div[class=jobdescription-row jobid]>div").get(1);
		if(jobE != null) job.setReferenceId(jobE.text().trim());
		jobE = doc.select("div[class=jobdescription-row addedon]>div").get(1);
		if(jobE != null) job.setPostedDate(parseDate(jobE.text().split(" ")[0].trim(), DF,DF1,DF2,DF3));
		jobE = doc.selectFirst("a[class=sf_applybtn]");
		if(jobE != null) job.setApplicationUrl(jobE.attr("href"));
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
