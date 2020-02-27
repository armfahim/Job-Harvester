package io.naztech.jobharvestar.scraper.icims;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All job site of Icims parsing class
 * 
 * <li><a href=
 * "https://careers-ageas-uk.icims.com/jobs/search?mobile=false&width=970&height=500&bga=true&needsRedirect=false&jan1offset=360&jun1offset=360">Ageas</a></li>
 * <li><a href="https://careers-arch.icims.com/jobs/search">Arch Capital
 * Group</a></li>
 * <li><a href="https://careers-berkley.icims.com/jobs/search">Berkley Wr
 * Corp</a></li>
 * <li><a href="https://careers-cit.icims.com/jobs/search">Cit Group</a></li>
 * <li><a href="https://careers-firstrepublic.icims.com/jobs/search?pr=0">First
 * Republic</a></li>
 * <li><a href=
 * "https://careers-foliofn.icims.com/jobs/search?ss=1&hashed=-435620354">Folio</a></li>
 * <li><a href=
 * "https://globalcareers-goldmansachs.icims.com/jobs/search?pr=0">Goldman
 * Sachs</a></li>
 * <li><a href="https://gwlcareers-greatwestlife.icims.com/jobs/search">Great
 * West Life Co</a></li>
 * <li><a href=
 * "https://careers-smbcgroup.icims.com/jobs/search?ss=1&searchKeyword=&searchCategory=&searchZip=&searchRadius=20">Sumitomo
 * Americas</a></li>
 * <li><a href="https://careers-vistajet.icims.com/jobs/search?pr=0">Vista
 * Jet</a></li>
 *
 * @author tohedul.islum
 * @author rahat.ahmad
 * @since 2019-02-27
 * 
 * 
 *        Changed the code structure & refactored. <br>
 *        Considering some item will miss because of unexpected behave of site.
 *        <br>
 *        Otherwise now job parsing is faster than previous. <br>
 *        And all the implementation is now under Abstract Class.
 * 
 * @author tanmoy.tushar
 * @since 2019-07-25
 */
@Service
public abstract class AbstractIcims extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected static final String CONTAINER_FRAME = "icims_content_iframe";
	private static final String TAILURL = "/jobs/search?pr=";
	private String baseUrl;
	protected int expectedJobCount = 0;
	protected int totalPage;
	protected int maxRetry = 0;
	protected Exception exception;
	protected int JOB_PER_PAGE = 20;
	protected static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		SiteMetaData site = getSiteMetaData(getSiteName());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().split("icims.com")[0].trim() + "icims.com";
		totalPage = getTotalPage(site.getUrl());
		if (totalPage > 5)
			expectedJobCount = totalPage * JOB_PER_PAGE;
		for (int i = 0; i < totalPage; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			String url = "";
			try {
				url = getBaseUrl() + TAILURL + i;
				browseJobList(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list of " + url, e);
				continue;
			}
		}
	}

	protected int getTotalPage(String url) throws IOException {
		try {
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
			String iFrameUrl = doc.getElementById(CONTAINER_FRAME).attr("src");
			doc = Jsoup.connect(iFrameUrl).get();
			Elements el = doc.select("h2[class=iCIMS_SubHeader iCIMS_SubHeader_Jobs]");
			String totalPage;
			if (el.size() == 1)
				totalPage = el.get(0).text();
			else
				totalPage = el.get(1).text();
			return Integer.parseInt(totalPage.split("of")[1].trim());
		} catch (IOException e) {
			log.error("Failed to parse total page, site layout changed, site exiting...", e);
			throw e;
		}
	}
	

	protected void browseJobList(String url, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		String iFrameUrl = doc.getElementById(CONTAINER_FRAME).attr("src");
		doc = Jsoup.connect(iFrameUrl).timeout(TIME_10S).get();
		Elements rowList = doc.select("div[class=col-xs-12 title]>a");
		if (totalPage < 6)
			expectedJobCount += rowList.size();
		for (Element row : rowList) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			String jobUrl = row.attr("href");
			String extrUrlLink = "?hub=7&mobile=false&width=760&height=500&bga=true&needsRedirect=false&jan1offset=360&jun1offset=360";
			Job job = new Job(jobUrl+extrUrlLink);
			try {
				saveJob(getJobDetail(job), site);
			}
			/**
			 * Sometime detail page got SocketTimeoutException.
			 * But If reload the page in browser, then it's working.
			 * To handle socketTimeoutException ,blindly reload this page 3 times.
			 */
			catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					log.info("Failed to load the detail page.. Reloading once again... " + job.getUrl(), e);
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

	protected Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		String iFrameUrl = doc.getElementById(CONTAINER_FRAME).attr("src");
		doc = Jsoup.connect(iFrameUrl).timeout(TIME_10S).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		Elements jobSpec = doc.select("div[class=iCIMS_Expandable_Container]");
		job.setSpec(jobSpec.get(0).text().trim());
		if (jobSpec.size() > 1)
			job.setPrerequisite(jobSpec.get(1).text().trim());
		job = getJobInfo(job, doc);
		return job;
	}

	protected Job getJobInfo(Job job, Document doc) {
		Element jobE = doc
				.selectFirst("a[class=iCIMS_Anchor iCIMS_Action_Button iCIMS_ApplyOnlineButton iCIMS_PrimaryButton]");
		if (jobE != null)
			job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("div[class=col-xs-6 header left]>span");
		if (jobE != null) {
			job.setLocation(jobE.nextElementSibling().text().trim());
		} else {
			jobE = doc.selectFirst("div[class=col-xs-6 header right]>span");
			if (jobE != null)
				job.setLocation(jobE.nextElementSibling().text().trim());
		}
		Elements jobInfo = doc.select("div[class=iCIMS_JobHeaderGroup]>dl");
		for (Element element : jobInfo) {
			if (element.text().contains("Job ID"))
				job.setReferenceId(element.text().split("ID")[1].trim());
			if (job.getReferenceId() == null && element.text().contains("ID"))
				job.setReferenceId(element.text().split("ID")[1].trim());
			if (element.text().contains("Schedule Type") || element.text().contains("Position Type")
					|| element.text().contains("Job Type"))
				job.setType(element.text().split("Type")[1].trim());
			if (element.text().contains("Type") && element.text().contains("Time"))
				job.setType(element.text().split("ype")[1].trim());
			if (element.text().contains("Business") && !element.text().contains("Company"))
				job.setCategory(element.text().split("ness")[1].trim());
			if (job.getCategory() == null && element.text().contains("Category"))
				job.setCategory(element.text().split("gory")[1].trim());
			if (job.getCategory() == null && element.text().contains("Department"))
				job.setCategory(element.text().split("ment")[1].trim());
			if (job.getLocation() == null && element.text().contains("Locations"))
				job.setLocation(element.text().split("Locations")[1].trim());
			if (job.getLocation() == null && element.text().contains("Location"))
				job.setLocation(element.text().split("cation")[1].trim());
		}
		return job;
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
