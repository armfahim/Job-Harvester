package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * Hargreaves Lansdown job site parser.<br>
 * URL: https://hargreaveslansdown.recruitee.com/
 * 
 * @author Armaan Seraj Choudhury
 * @author iftekar.alam
 * @since 2019-02-14
 */
@Slf4j
@Service
public class HargreavesLansdown extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.HARGREAVES_LANSDOWN;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 40);
		Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		try {
			getSummaryPage(doc, siteMeta);
		} catch (Exception e) {
			log.warn("Failed to parse list of " + e);
		}
	}

	private void getSummaryPage(Document doc, SiteMetaData siteMeta) throws InterruptedException{
		Elements rowList = doc.select("div[class=col-md-8 col-xs-12 col-centered]");
		expectedJobCount=rowList.size();
		for (Element el : rowList) {
			Job job=new Job(getBaseUrl() + el.select("h5>a").attr("href"));
			job.setTitle(el.select("h5>a").text().trim());
			job.setName(job.getTitle());
			job.setCategory(el.select("div[class=department]").text().trim());
			job.setLocation(el.select("ul>li").text().trim());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse details of " + e);
				exception=e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setSpec(doc.selectFirst("div[class=description]").text().trim());
		job.setPrerequisite(doc.select("div[class=description]").get(1).text().trim());
		job.setApplicationUrl(getBaseUrl()+doc.selectFirst("a[class=btn btn-thebiggest]").attr("href"));
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