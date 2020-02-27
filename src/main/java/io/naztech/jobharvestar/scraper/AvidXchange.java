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
 * AvidXchange jobs site parse <br>
 * URL: https://usr55.dayforcehcm.com/CandidatePortal/en-us/avidxchange
 * 
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-03-11
 */
@Service
@Slf4j
public class AvidXchange extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AVIDXCHANGE;
	private static final DateTimeFormatter DF_1 = DateTimeFormatter.ofPattern("MMMM dd yyyy");
	private static final DateTimeFormatter DF_2 = DateTimeFormatter.ofPattern("MMMM d yyyy");
	private String baseUrl;
	private int expectedJobCount=0;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 29);
		Document doc = Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		int totalPage=getTotalPages(doc);
		for (int i = 1; i <= totalPage; i++) {
			try {
				browseJobList(site.getUrl()+"?page="+i,site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + getBaseUrl()+"?page="+i, e);
			}
		}
	}
	
	private int getTotalPages(Document doc) {
		Elements totalPageElement = doc.select("nav[class=pagination]>ul>li");
		String totalPage=totalPageElement.get(totalPageElement.size()-2).text();
		return Integer.parseInt(totalPage);
	}

	private void browseJobList(String url,SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements jobRowList =doc.select("li[class=search-result ]");
		expectedJobCount += jobRowList.size();
		for (Element row : jobRowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + row.getElementsByTag("div").get(0).children().attr("href"));
			job.setTitle(row.getElementsByTag("div").get(0).children().text().trim());
			job.setName(job.getTitle());
			job.setReferenceId(row.getElementsByTag("div").get(1).child(1).text().split("#")[1].trim());
			String[] postDate=row.getElementsByTag("div").get(4).text().split(",");
			job.setPostedDate(parseDate(postDate[1].trim()+" "+postDate[2].trim(),DF_1,DF_2));
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
		Element jobE = doc.selectFirst("a[class=btn-primary]");
		job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		jobE = doc.selectFirst("span[class=job-location]");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("ul[class=job-posting-items]>li");
		if (jobE != null && jobE.text().contains("Job Family")) 
			job.setCategory(jobE.text().replace("Job Family", "").trim());
		Elements spec = doc.select("div[class=job-posting-section]");
		if(spec.size()>1) job.setSpec(spec.get(1).text());
		else job.setSpec(spec.text());
		return job;
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
