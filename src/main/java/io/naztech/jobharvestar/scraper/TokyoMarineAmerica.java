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
 * Tokyo Marine Holdings America
 * URL: http://careers.tmamerica.com/search/jobs
 * 
 * @author Armaan Seraj Choudhury
 * @author bm.alamin
 * @author tanmoy.tushar
 * @since 2019-02-10
 */
@Slf4j
@Service
public class TokyoMarineAmerica extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TOKYO_MARINE_AMERICA;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private int expectedJobCount;
	private Exception exception;
	private static final String BASE_XPATH = "div[class=jobs-section__item page-section-small]>div";
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.TOKYO_MARINE_AMERICA));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 27);
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements rowList = doc.select(BASE_XPATH + ">div[class=large-6 columns]>h5>a");
		Elements locList = doc.select(BASE_XPATH + ">div[class=large-4 columns]");
		Elements dateList = doc.select(BASE_XPATH + ">div[class=large-2 columns]");
		expectedJobCount = rowList.size();
		browseJobList(rowList, locList, dateList, siteMeta);
		
	}

	private void browseJobList(Elements rowList, Elements locList, Elements dateList, SiteMetaData site) throws InterruptedException{
		for (int i = 0; i < rowList.size(); i++) {
			String url = rowList.get(i).attr("href");
			Job job = new Job(rowList.get(i).attr("href"));
			job.setLocation(locList.get(i).text().split(":")[1].trim());
			job.setPostedDate(parseDate(dateList.get(i).text().split(":")[1].trim(), DF));
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());		
		jobE = doc.selectFirst("div[class=page-section-medium space-medium]");
		job.setSpec(jobE.text().trim());		
		jobE = doc.selectFirst("a[class=cs_item_apply_button_link]");
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
