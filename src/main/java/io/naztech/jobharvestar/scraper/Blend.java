package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Blend job site parser <br>
 * URL: https://blend.com/careers/
 * 
 * @author Arifur Rahman
 * @author iftekar.alam
 * @since 2019-03-24
 */
@Slf4j
@Service
public class Blend extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BLEND;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc=Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("li[class=job-title]>a");
		expectedJobCount= rowList.size();
		for (Element el : rowList) {
			Job job=new Job(el.attr("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws MalformedURLException, IOException {
		Document doc=Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getName());
		try {
			job.setSpec(doc.selectFirst("div[class=opening-item]").text().trim());
			job.setPrerequisite(doc.select("div[class=opening-item]").get(1).text().trim());
		} catch (Exception e) {
			job.setSpec(doc.selectFirst("div[class=opening-details]").text().trim());
		}
		Element jobE = doc.selectFirst("span[class=opening-location]");
		if (jobE != null) job.setLocation(jobE.text().trim());
	    jobE = doc.selectFirst("span[class=opening-team]");
	    if (jobE != null) job.setCategory(jobE.text().trim());	
	    jobE = doc.selectFirst("span[class=opening-commitment]");
	    if (jobE != null) job.setType(jobE.text().trim());
	    jobE = doc.selectFirst("a[class=action tertiary]");
	    if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
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
