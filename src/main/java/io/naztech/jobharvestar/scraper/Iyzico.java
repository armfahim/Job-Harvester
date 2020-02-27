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
 * Iyzico jobs site parser<br>
 * URL: https://iyzico.recruitee.com/
 * 
 * @author oyndrila.chowdhury
 * @author iftekar.alam
 * @since 2019-03-31
 */
@Slf4j
@Service
public class Iyzico extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.IYZICO;
	private static String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc=Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("a[class=col-md-6]");
		expectedJobCount=rowList.size();
		for (int i = 0; i < rowList.size(); i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(site.getUrl().substring(0, 28) + rowList.get(i).attr("href"));
			job.setCategory(rowList.get(i).select("div>div").get(1).text().trim());
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception=e;
				log.warn(" failed to parse details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h2").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.select("div[class=description]").get(0).text().trim());
		job.setPrerequisite(doc.select("div[class=description]").get(1).text().trim());
		Element jobE = doc.selectFirst("a[class=btn btn-thebiggest]");
		if(jobE !=null) job.setApplicationUrl(job.getUrl().substring(0, 28)+jobE.attr("href"));
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