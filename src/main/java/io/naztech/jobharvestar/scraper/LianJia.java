package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * LianJia job site scrapper.<br>
 * URL: http://join.lianjia.com/search?k=
 * 
 * @author shadman.shahriar
 * @since 2019-03-24
 */
@Slf4j
@Service
public class LianJia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.LIANJIA_HOMELINK;
	private String baseUrl;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount;
	private Exception exception;
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		browseJobList(siteMeta);
	}
	
	private void browseJobList(SiteMetaData siteMeta) throws InterruptedException, IOException {
		try {
			Document doc = Jsoup.connect(siteMeta.getUrl()).get();
			Elements jobList= doc.getElementsByTag("tbody").select(" > *");
			expectedJobCount = jobList.size();
			for (int i = 1; i < jobList.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job=new Job(getBaseUrl()+jobList.get(i).child(0).child(0).attr("href"));
				job.setTitle(jobList.get(i).child(0).child(0).text().trim().trim().split("\\(")[0].trim());
				job.setReferenceId(jobList.get(i).child(0).child(0).text().trim().split("\\(")[1].trim().split("\\)")[0].trim());
				job.setName(job.getTitle());
				job.setCategory(jobList.get(i).child(1).text().trim());
				job.setLocation(jobList.get(i).child(2).text().trim());
				job.setPostedDate(parseDate(jobList.get(i).child(3).text().trim(), DF));
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			log.warn("Failed to find element ",e);
			throw e;
		}
	}
	
	public Job getJobDetails(Job job) throws IOException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			job.setApplicationUrl(getBaseUrl()+doc.selectFirst("#apply").attr("url"));
			job.setSpec(doc.selectFirst(".xiangqingtext").text().trim());
			return job;
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			log.warn("Failed to parse job details of "+job.getUrl() , e);
		}
		return null;
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
