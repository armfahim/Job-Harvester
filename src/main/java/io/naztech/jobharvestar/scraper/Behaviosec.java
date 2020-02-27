package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
 * Behaviosec jobs site parser <br>
 * URL: https://www.behaviosec.com/careers/
 * 
 * @author sohid.ullah
 * @author iftekar.alam
 * @since 2019-03-27
 */
@Service
@Slf4j
public class Behaviosec extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BEHAVIOSEC;
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("yyyy-MM-d");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc=Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements jobList = doc.select("div[class=c-card-inner]");
		expectedJobCount=jobList.size();
		for (Element el : jobList) {
			Job job=new Job(el.getElementsByTag("a").attr("href"));
			job.setTitle(el.getElementsByTag("h2").text().trim());
			job.setName(job.getTitle());
			job.setLocation(el.getElementsByTag("p").get(0).text().split(":")[1].trim());
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail page of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setSpec(doc.selectFirst("div[class=blog_content]").text().trim());
		Element el = doc.selectFirst("a[class=primary_btn]");
		if(el != null) job.setApplyEmail(el.attr("href").split(":")[1].trim());
		el = doc.selectFirst("ul[class=date-time]>li>time");
		if(el != null) job.setPostedDate(parseDate(el.attr("datetime"), DF1, DF2));
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
