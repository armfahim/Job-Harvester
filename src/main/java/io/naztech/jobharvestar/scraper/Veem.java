package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.regex.Pattern;

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
 * Veem job site scraper. <br>
 * URL: https://www.veem.com/careers/openings/
 * 
 * @author muhammad.tarek
 * @author tanmoy.tushar
 * @since 2019-04-02
 */
@Slf4j
@Service
public class Veem extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.VEEM;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements jobList = doc.select("a[class=btn btn-primary btn-sm ml-auto]");
		expectedJobCount = jobList.size();
		browseJobList(jobList, siteMeta);
	}

	private void browseJobList(Elements jobList, SiteMetaData site) {
		for (Element el : jobList) {
			String url = el.attr("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + url, e);
			}
		}		
	}

	protected Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		String[] parts =  doc.selectFirst("h5").text().trim().split(Pattern.quote("|"));
		if (parts.length > 1) {
			job.setLocation(parts[0].trim());
			job.setCategory(parts[1].trim());			
		}
		Elements jobSpec = doc.select("div[class=row]>div>ul");
		if (jobSpec.size() > 1) {
			job.setSpec(jobSpec.get(0).text().trim());
			job.setPrerequisite(jobSpec.get(1).text().trim());
		}
		else {
			job.setSpec(doc.selectFirst("div[class=col-12 large-text single-blog-post post-24627 page type-page status-publish hentry]").text().trim());
		}
		job.setApplicationUrl(job.getUrl() + "#apply");
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
