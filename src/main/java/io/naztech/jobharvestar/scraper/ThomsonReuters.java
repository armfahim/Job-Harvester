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
 * Thomson Reuters Job Site Scraper.<br>
 * URL: http://jobs.thomsonreuters.com/ListJobs/All/Page-1
 * 
 * @author Mahmud Rana
 * @author iftekar.alam
 * @since 2019-02-12
 */
@Service
@Slf4j
public class ThomsonReuters extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.THOMSON_REUTERS_CORP;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private int maxRetry = 0;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 31);
		int totalPage = getTotalPage(site.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			String url = site.getUrl() + "/Page-" + i;
			try {
				browseJobList(url, site);
				maxRetry=0;
			}/**
			 * Sometime browse page got SocketTimeoutException. But If reload the page in
			 * browser, then it's working. To handle socketTimeoutException ,blindly reload
			 * this page 3 times.
			 */
			catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					browseJobList(url, site);
				} else {
					log.warn("Failed to parse job list page of " + url, e);
				}
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws IOException ,SocketTimeoutException{
		Document doc=Jsoup.connect(url).get();
		Elements jobList = doc.select("td[class=coloriginaljobtitle]>a");
		for (Element el : jobList) {
			Job job = new Job(getBaseUrl() + el.attr("href"));
			try {
				saveJob(getJobDetail(job), site);
				maxRetry = 0;
			} 
			/**
			 * Sometime detail page got SocketTimeoutException. But If reload the page in
			 * browser, then it's working. To handle socketTimeoutException ,blindly reload
			 * this page 3 times.
			 */
			catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					saveJob(getJobDetail(job), site);
				} else {
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException,SocketTimeoutException {
		Document doc=Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("div.jd-left > h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		Element details = doc.selectFirst("div.job-fields");
		if (details.child(1).text().split(":")[1].trim().contains("-")
				&& details.child(1).text().trim().split(":")[1].contains("|"))
			job.setLocation(details.child(1).text().trim().split(":")[1].replace("-", ",").replace("|", ",").trim());
		else if (details.child(1).text().trim().split(":")[1].contains("-")) 
			job.setLocation(details.child(1).text().trim().split(":")[1].replace("-", ",").trim());
		else if (details.child(1).text().trim().split(":")[1].contains("|"))
			job.setLocation(details.child(1).text().trim().split(":")[1].replace("|", ",").trim());
		else job.setLocation(details.child(1).text().trim().split(":")[1].trim());
		job.setReferenceId(details.child(0).text().split(":")[1].trim());
		job.setCategory(details.child(3).text().trim());
		Element jobS = doc.selectFirst("div.desc");
		job.setSpec(jobS.text().trim());
		Element jobA = doc.selectFirst("div.applyBtnBottomDiv>a");
		job.setApplicationUrl(jobA.attr("href"));
		return job;
	}

	private int getTotalPage(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Element el = doc.select("span.pager_counts").get(0);
		String totalJob = el.text().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
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
