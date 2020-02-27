package io.naztech.jobharvestar.scraper.selenium;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Pinterest job site parser. <br>
 * URL: https://www.pinterestcareers.com/jobs/search?page=
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-03-13
 */
@Service
@Slf4j
public class Pinterest extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PINTEREST;
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
		int totalPage=getTotalPage(siteMeta);
		for (int i = 1; i <= totalPage ; i++) {
			String nextPageUrl=siteMeta.getUrl()+i;
			try {
				getSummaryPages(nextPageUrl,siteMeta);
			} catch (Exception e) {
				exception=e;
				log.warn("Failed to parse list of " + nextPageUrl,e);
			}
		}
	}
	public void getSummaryPages(String url,SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("table[class=table]>tbody>tr");
		for (Element el : rowList) {
			Job job= new Job(el.getElementsByTag("td").get(0).getElementsByTag("a").get(0).attr("href"));
			job.setTitle(el.getElementsByTag("td").get(0).getElementsByTag("a").get(0).text().trim());
			job.setName(job.getTitle());
			job.setCategory(el.getElementsByTag("td").get(1).text().trim());
			job.setLocation(el.getElementsByTag("td").get(2).text().trim());
			try {
				saveJob(getJobDetails(job), siteMeta);					
			} catch(Exception e) {
				log.warn("Failed to parse details of " + job.getUrl(),e);
				exception = e;
			}
		}
	}
	
	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setSpec(doc.selectFirst("div[id=job_description_2_0]>ul").text().trim());
		Element  pre=doc.select("div[id=job_description_2_0]>ul").get(1);
		if (pre != null) job.setPrerequisite(pre.text().trim());
		return job;
	}

	private int getTotalPage(SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		String totalJob = doc.select("div[class=table-counts]>p>b").get(1).text();
		expectedJobCount=Integer.parseInt(totalJob);
		return getPageCount(totalJob, 30);
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
