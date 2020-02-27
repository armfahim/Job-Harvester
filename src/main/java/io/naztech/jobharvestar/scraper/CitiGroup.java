package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.TimeoutException;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Citi Group job site parser<br>
 * URL: https://jobs.citi.com/search-jobs?k=&ac=&industry=&Country=&State=&City=&orgIds=287&p=
 *  https://citi.taleo.net/careersection/2/joblist.ftl
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-01-29
 */
@Service
@Slf4j
public class CitiGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CITIGROUP_INC;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private int maxRetry = 0;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 21);
		for (int i = 1; i <= getTotalPage(site); i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			String nextPageUrl=site.getUrl()+i;
			try {
				browseJobList(nextPageUrl,site);
			} catch (Exception e) {
				log.warn("Failed to parse list of " + nextPageUrl , e);
			}
		}
	}
	
	private void browseJobList(String url,SiteMetaData site) throws IOException, PageScrapingInterruptedException  {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M*2).get();
		Elements rowList = doc.select("section[id=search-results-list]>ul>li>a");
		for (Element el : rowList) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl=baseUrl + el.attr("href");
			Job job=new Job(jobUrl);
			try {
				saveJob(getJobDetailType1(job), site);
			} catch (NullPointerException e) {
				saveJob(getJobDetailType2(job), site);
			}
			/**
			 * Sometime detail page got SocketTimeoutException. But If reload the page in
			 * browser, then it's working. To handle socketTimeoutException ,blindly reload
			 * this page 3 times.
			 */
			catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					saveJob(getJobDetailType1(job), site);
				} else {
					log.warn("Failed to parse job detail of " + jobUrl, e);
				}
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + jobUrl, e);
				exception = e;
			}
		}
	}
	
	/**
	 * 2 type of Details page(2 type of layout) exist for City Group
	 */
	private Job getJobDetailType1(Job job) throws IOException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M*2).get();
		    job.setTitle(doc.select("h2").get(1).text().trim());
			job.setName(job.getTitle());
			job.setSpec(doc.selectFirst("span[class=job-description2 job-info]").text().trim());
			Element jobE=doc.selectFirst("span[class=location job-info]");
			if (jobE!=null) job.setLocation(jobE.text().split(":")[1].trim());
			jobE=doc.selectFirst("span[class=category job-info]");
			if (jobE!=null) job.setCategory(jobE.text().split(":")[1].trim());
			jobE=doc.select("span[class=emplyee-status job-info]").get(1);
			if (jobE!=null) job.setReferenceId(jobE.text().split(":")[1].trim());
			jobE=doc.selectFirst("a[class=button job-apply top]");
			if (jobE!=null) job.setApplicationUrl(jobE.attr("href"));
		} catch (UnknownHostException | TimeoutException e) {
			log.warn("Failed to parse job detail of " + job.getUrl(), e);
			exception = e;
		}
		return job;
	}
	
	private Job getJobDetailType2(Job job) throws IOException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M*2).get();
			job.setTitle(doc.select("h1").get(1).text().trim());
			job.setName(job.getTitle());
			job.setSpec(doc.selectFirst("div[class=ats-description ajd_job-details__ats-description]").text().trim());
			Element jobE = doc.selectFirst("p[class=ajd_header__location]");
			if (jobE!=null) job.setLocation( jobE.text().trim());
			jobE = doc.selectFirst("a[class=ajd_btn__apply button job-apply top]");
			if (jobE!=null) job.setApplicationUrl(jobE.attr("href"));
		} catch (UnknownHostException | TimeoutException | NullPointerException e) {
			log.warn("Failed to parse job detail of " + job.getUrl(), e);
			exception = e;
		}
		return job;
	}
	
	private int getTotalPage(SiteMetaData site) throws IOException {
		Document doc = Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M*2).get();
		String totalJob = doc.selectFirst("section[id=search-results]>h1").text().split(" ")[0].trim();
		expectedJobCount=Integer.parseInt(totalJob);
		return getPageCount(totalJob, 15);
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
