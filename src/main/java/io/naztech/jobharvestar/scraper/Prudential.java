package io.naztech.jobharvestar.scraper;

import java.io.IOException;

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
 * PRUDENTIAL FINANCIAL <br>
 * URL: http://jobs.prudential.com/job-listing.php
 * 
 * @author Rahat Ahmad
 * @author iftekar.alam
 * @since 2019-02-12
 */
@Slf4j
@Service
public class Prudential extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PRUDENTIAL;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.PRUDENTIAL));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException{
		Document doc;
		try {
			doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			this.baseUrl = siteMeta.getUrl().substring(0, 26);
			Elements jobList = doc.select("div[data-search=result]>div");
			expectedJobCount=jobList.size();
			for (Element el : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job=new Job(getBaseUrl() + el.child(0).attr("href"));
				Elements tr = el.select("ul[class=detailList clearfix]>li");
				String type = tr.get(0).text();
				if (type.contains("Position")) job.setType(type.replace("Position", ""));
				else job.setType(type);
				job.setReferenceId(tr.get(1).text().split(":")[1].trim());
				try {
					saveJob(getJobDetails(job),siteMeta);
				} catch (Exception e) {
					exception = e;
					log.warn(" failed to parse detail page of " + job.getUrl(), e);
				}
			}
		} catch (IOException e) {
			log.warn(getSiteName()+" fails to load page. ", e);
			throw e;
		}
	}
	
	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobTitle = doc.selectFirst("h1[class=title]");
		job.setTitle(jobTitle.text().replace(jobTitle.child(0).text(),"").trim());
		job.setName(job.getTitle());
		job.setLocation(jobTitle.child(0).text().split("-")[1].trim());
		job.setCategory(jobTitle.child(0).text().split("-")[0].trim());
		Element jobDetails = doc.selectFirst("div[class=joinDataLeftCol]");
		job.setSpec(jobDetails.text().trim());
		Element jobApplicationUrl = doc.selectFirst("div[class=applyBtnHolderWide hidden-xs]>a");
		job.setApplicationUrl(jobApplicationUrl.attr("href"));
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
