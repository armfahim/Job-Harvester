package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

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
 * Credit Agricole Group job site parser. <br>
 * URL: https://jobs.ca-cib.com/job/list-of-all-jobs.aspx?all=1&mode=list#ancrecontenu
 * 
 * @author Benajir Ullah
 * @author tanmoy.tushar
 * @since 2019-01-20
 */
@Service
@Slf4j
public class CreditAgricole extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CREDIT_AGRICOLE;
	private static final DateTimeFormatter DF =  DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("M/dd/yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter DF4 = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DF5 = DateTimeFormatter.ofPattern("MM/d/yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		int totalPage = getTotalPage(doc);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/job/list-of-all-jobs.aspx?page=" + i;
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData siteMeta) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("a[class=ts-offer-card__title-link  ]");
		for (Element row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + row.attr("href"));
			job.setTitle(row.text().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.getElementById("fldjobdescription_contract");
		if (jobE != null && jobE.text().length() > 5) job.setType(jobE.text().trim());
		jobE = doc.getElementById("fldjobdescription_primaryprofile");
		if (jobE != null && jobE.text().contains("-")) job.setCategory(jobE.text().split("-")[1].trim());
		jobE = doc.getElementById("fldlocation_location_geographicalareacollection");
		if (jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.getElementById("fldlocation_joblocation");
		if (jobE != null && job.getLocation() != null) job.setLocation(job.getLocation() + ", " + jobE.nextElementSibling().text().trim()); 
		jobE = doc.getElementById("fldjobdescription_description1");
		job.setSpec(jobE.text().trim());
		job.setPrerequisite(getPreRequisite(doc));
		job = getRefIdAndDate(job, doc);
		jobE = doc.getElementById("fldjobdescription_date1");
		if (jobE != null) job.setDeadline(parseDate(jobE.text().trim(), DF, DF1,DF3, DF4, DF5));
		return job;
	}

	private String getPreRequisite(Document doc) {
		String preRequisite = "";
		Element jobE = doc.getElementById("fldapplicantcriteria_educationlevel");
		if (jobE != null) preRequisite = jobE.previousElementSibling().text() + "\n" + jobE.text();
		jobE = doc.getElementById("fldapplicantcriteria_longtext2");
		if (jobE != null) preRequisite += "\n" + jobE.previousElementSibling().text() + "\n" + jobE.text();
		jobE = doc.getElementById("fldapplicantcriteria_experiencelevel");
		if (jobE != null) preRequisite += "\n" + jobE.previousElementSibling().text() + "\n" + jobE.text();
		jobE = doc.getElementById("fldapplicantcriteria_longtext1");
		if (jobE != null) preRequisite += "\n" + jobE.previousElementSibling().text() + "\n" + jobE.text();
		jobE = doc.getElementById("fldapplicantcriteria_freecriteria1");
		if (jobE != null) preRequisite += "\n" + jobE.previousElementSibling().text() + "\n" + jobE.text();
		jobE = doc.getElementById("fldapplicantcriteria_freecriteria2");
		if (jobE != null) preRequisite += "\n" + jobE.previousElementSibling().text() + "\n" + jobE.text();
		return preRequisite;
	}
	
	private Job getRefIdAndDate(Job job, Document doc) {
		Element jobE = doc.selectFirst("div[class=ts-offer-page__reference]");
		if (jobE != null) {
			String ref = jobE.text().trim();
			if (ref.contains("number")) job.setReferenceId(ref.split("number")[1].trim());
			else if (ref.contains("rence") && !ref.contains("number")) job.setReferenceId(ref.split("rence")[1].trim());
		}
		jobE = doc.selectFirst("div[class=ts-offer-page__maj-date]");
		if (jobE != null && jobE.text().contains("date"))
			job.setPostedDate(parseDate(jobE.text().split("date")[1].trim(), DF1, DF2, DF3, DF4, DF5));
		return job;
	}

	private int getTotalPage(Document doc) {
		Element jobE = doc.getElementById("ctl00_ctl00_corpsRoot_corps_Pagination_TotalOffers");
		String totalJob = jobE.text().trim().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 100);
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