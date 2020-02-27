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
 * Unison Jobsite Parser<br>
 * URL: https://www.unison.org.uk/about/jobs/
 * 
 * @author iftekar.alam
 * @since 2019-03-25
 */
@Service
@Slf4j
public class Unison extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNISON;
	private String baseUrl;
	private int expectedJobCount=0;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMMM yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("d MMMM yyyy");
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S).get();
		Elements jobList = doc.select("div[class=entry-content-wrap]");
		expectedJobCount = jobList.size();
		jobList.forEach(el->{Job job = new Job(el.select("div>h1>a").attr("href"));
		try {
			if(el.select("div>dl>dt").get(3).text().trim().contains("Closing date")) job.setDeadline(parseDate(el.select("div>dl>dd").get(3).text().trim(),DF,DF1));
		} catch (Exception e) {
			if(el.select("div>dl>dt").get(2).text().trim().contains("Closing date")) job.setDeadline(parseDate(el.select("div>dl>dd").get(2).text().trim(),DF,DF1));
		}
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				log.warn("Faild to parse details of "+ getBaseUrl(),e);
				exception = e;
			} });
	}
	
	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S).get();
		Element title = doc.selectFirst("h1[class=entry-title]");
		job.setTitle(title.text().trim());
		job.setName(job.getName());
		Elements tl = doc.select("dl[class=job-details list-details]");
		job.setLocation(tl.get(0).getElementsByTag("dd").get(0).text());
		if (tl.get(0).getElementsByTag("dd").get(4).text().contains(",")) job.setType(tl.get(0).getElementsByTag("dd").get(4).text().split(",")[0].trim());
		else job.setType(tl.get(0).getElementsByTag("dd").get(4).text().trim());
		Element el = doc.selectFirst("div[class=entry-content entry-content-single]");
		job.setSpec(el.text().trim());
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
