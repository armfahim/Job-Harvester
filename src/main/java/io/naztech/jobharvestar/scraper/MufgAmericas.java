package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Americas Jobsite Parser <br>
 * URL: https://jobs.mufgamericas.com/location/united-states-jobs/29757/6252001/2/
 * 
 * @author Mahmud Rana
 * @since 2019-02-17
 * 
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-04-18
 */
@Slf4j
@Service
public class MufgAmericas extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MUFG_BANK_AMERICAS;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private String baseUrl;
	private int expectedJobCount=0;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.MUFG_BANK_AMERICAS));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 29);
		Document doc = Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		int totaljob = Integer.parseInt(doc.selectFirst("span[class=pagination-total-pages]").text().split("of")[1].trim());
		for (int i = 1; i <= totaljob; i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			String pageUrl=site.getUrl()+i;
			try {
				browseJobList(pageUrl,site);
			} catch (Exception e) {
				log.warn("Failed to parse page of "+pageUrl);
			}
		}
	}

	private void browseJobList(String pageUrl , SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(pageUrl).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("section[id=search-results-list]>ul>li>a");
		expectedJobCount +=rowList.size();
		for (Element el : rowList) {
		    if(isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(getBaseUrl()+el.attr("href"));
			job.setTitle(el.getElementsByTag("h2").text().trim());
			job.setName(job.getTitle());
			job.setLocation(el.getElementsByTag("span").get(0).text().trim());
			job.setPostedDate(parseDate(el.getElementsByTag("span").get(1).text().trim(),DF));
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			job.setSpec(doc.selectFirst("div[id=requisitionDescriptionInterface.ID1604.row1]").text().trim());
			job.setPrerequisite(doc.selectFirst("div[id=requisitionDescriptionInterface.ID1666.row1]").text().trim());
		    Element jobE = doc.selectFirst("span[id=requisitionDescriptionInterface.ID1771.row1]");
			if(jobE != null)  job.setCategory(jobE.text().trim());
			jobE = doc.selectFirst("span[id=requisitionDescriptionInterface.ID2035.row1]");
			if(jobE != null)  job.setType(jobE.text().trim());
			jobE = doc.selectFirst("span[class=job-id job-info]");
			if(jobE != null)  job.setReferenceId(jobE.text().split("ID")[1].trim());
			jobE = doc.selectFirst("a[class=button job-apply top]");
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