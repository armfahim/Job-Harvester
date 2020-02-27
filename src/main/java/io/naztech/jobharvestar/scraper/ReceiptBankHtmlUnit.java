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

 * ReceiptBank Job site parser<br>
 * URL: https://www.receipt-bank.com/careers/all-jobs/?careers-location&careers-department&careers-keywords
 * 
 * @author Muhammad Bin Farook
 * @author iftekar.alam
 * @since 2019-03-24
 */
@Slf4j
@Service
public class ReceiptBankHtmlUnit extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.RECEIPT_BANK;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 28);
		Document doc=Jsoup.connect(siteMeta.getUrl()).get();
		int totalPage=getTotalPage(doc);
		for (int i = 1; i <= totalPage; i++) {
			String url=getBaseUrl()+"/careers/all-jobs/"+i;
			try {
				getSummaryPages(url,siteMeta);			
			} catch (Exception e) {
				log.warn("Failed to parse joblist page of "+url,e);
			}
		}
	}

	private void getSummaryPages(String url,SiteMetaData siteMeta) throws IOException {
		Document doc=Jsoup.connect(url).get();
		Elements jobList = doc.select("div[class=job-listings__item]>a");
		for (Element el : jobList) {
			Job job=new Job(getBaseUrl() + el.attr("href"));
			job.setTitle(el.child(0).text().trim());
			job.setName(job.getTitle());
			job.setLocation(el.child(1).text().trim());
			try {
				saveJob(getJobDetails(job), siteMeta);						
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details page of "+url,e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.selectFirst("section[class=workable__single-listing]").text().trim());
		return job;
	}
	
	private int getTotalPage(Document doc) {
		Element totalJobXPath=doc.selectFirst("h4[class=workable__job-total]");
		String totalJob=totalJobXPath.text().split("We currently have")[1].trim().split("openings")[0].trim();
		expectedJobCount=Integer.parseInt(totalJob);
		return getPageCount(totalJob, 12);
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
