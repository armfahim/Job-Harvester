package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;

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
 * IBM Services jobs site parser <br>
 * URL: https://careers.ibm.com/ListJobs/All/?lang=en
 * 
 * @author fahim.reza
 * @since 2019-10-20
 */
@Slf4j
@Service
public class IbmServices extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.IBM_SERVICES;
	private String baseUrl;
	private static final int JOB_PER_PAGE = 30;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException{
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		try {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		String totalJob = getTotalJob(doc);
		if (totalJob == null) {
			throw new NullPointerException("Total page number not found");
		}
		expectedJobCount = Integer.parseInt(totalJob);
		log.info("Total Job Found: " + getExpectedJob());
		int totalPage = getPageCount(totalJob, JOB_PER_PAGE);

		for (int i = 1; i <= totalPage; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			getSummaryPage(baseUrl + "/ListJobs/All/Page-" + i + "/?lang=en", siteMeta);
		}
		}catch(SocketTimeoutException e) {
			log.warn("Socket Time out Exception",e);
		}

	}

	private void getSummaryPage(String url, SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Job job = new Job();
		try {
			Elements jobUrl = doc.select("table[class=JobListTable] > tbody > tr >  td > a");
			for (Element el : jobUrl) {
				job.setUrl(baseUrl + el.attr("href"));
				job.setTitle(el.text());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job details of : " + job.getUrl());
				}

			}
		} catch (Exception e) {
			exception = e;
			log.warn("Failed to parse summary page of : " + url);
		}

	}

	private Job getJobDetails(Job job) throws IOException,SocketTimeoutException {
		Document doc 	     = Jsoup.connect(job.getUrl()).get();
		Elements detailsList = doc.select("div[class=job-specs mobile-only] > ul > li");
		Element applyUrl 	 = doc.selectFirst("div[class=applyBtnTopDiv] > a");
		Elements desc        = doc.select("div[id=job-description]");
		String country       = detailsList.get(0).text().split(":")[1].trim();
		String state         = detailsList.get(1).text().split(":")[1].trim();
		String city          = detailsList.get(2).text().split(":")[1].trim();
		job.setCategory(detailsList.get(3).text().split(":")[1].trim());
		job.setType(detailsList.get(6).text().split(":")[1].trim());
		job.setLocation(city + "," + state + "," + country);
		job.setReferenceId(detailsList.get(8).text().split(":")[1].trim());
		job.setApplicationUrl(applyUrl.attr("href"));
		job.setSpec(desc.text());
		return job;
	}

	private String getTotalJob(Document doc) throws IOException,SocketTimeoutException {
		Element totalJob = doc.selectFirst("span[class=pager_counts]");
		return totalJob.text().split("of")[1].trim();
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
