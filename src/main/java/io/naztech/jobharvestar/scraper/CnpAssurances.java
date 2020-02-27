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
 * CNP Assurances job site parser. <br>
 * URL: https://cnp-recrute.talent-soft.com/offre-de-emploi/liste-offres.aspx
 * 
 * @author Benajir Ullah
 * @author tanmoy.tushar
 * @since 2019-02-14
 */
@Slf4j
@Service
public class CnpAssurances extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CNP_ASSURANCES;
	private static final int JOBS_PER_PAGE = 10;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.CNP_ASSURANCES));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 35);
		Document doc = loadPage(site.getUrl());
		int totalPage = getTotalPage(doc);
		Elements rowList = doc.select("a[class=ts-offer-card__title-link]");
		Elements infoList = doc.select("ul[class=ts-offer-card-content__list ]");
		browseJobList(rowList, infoList, site);
		for (int i = 2; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = site.getUrl() + "?page=" + i + "&LCID=1036";
			try {
				doc = loadPage(url);
				rowList = doc.select("a[class=ts-offer-card__title-link]");
				infoList = doc.select("ul[class=ts-offer-card-content__list ]");
				browseJobList(rowList, infoList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, Elements infoList, SiteMetaData site) throws InterruptedException {
		for(int i = 0; i < rowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + rowList.get(i).attr("href"));
			job.setTitle(rowList.get(i).text().trim());
			job.setName(job.getTitle());
			job.setReferenceId(infoList.get(i).getElementsByTag("li").get(0).text());
			job.setPostedDate(parseDate(infoList.get(i).getElementsByTag("li").get(1).text(), DF));
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = loadPage(job.getUrl());
		job.setSpec(doc.getElementById("fldjobdescription_description1").text() 
				+ "\n" + doc.getElementById("fldjobdescription_description2").text());
		job.setCategory(doc.getElementById("fldjobdescription_primaryprofile").text());
		job.setType(doc.getElementById("fldjobdescription_contract").text());
		job.setLocation(doc.getElementById("fldlocation_location_geographicalareacollection").text());
		job.setPrerequisite(getPrerequisite(doc));
		return job;
	}
	
	private String getPrerequisite(Document doc) {
		String requitiste = "";
		Element preReq = doc.getElementById("fldapplicantcriteria_educationlevel");
		if (preReq != null) requitiste = preReq.text();
		preReq = doc.getElementById("fldapplicantcriteria_experiencelevel");
		if (preReq != null && !requitiste.equals("")) requitiste = requitiste + "\n" +preReq.text();
		preReq = doc.getElementById("fldapplicantcriteria_requiredlanguagecollection");
		if (preReq != null && !requitiste.equals("")) requitiste = requitiste + "\n" +preReq.text();
		return requitiste;
	}

	private int getTotalPage(Document doc) {
		String totalJob = doc.getElementById("ctl00_ctl00_corpsRoot_corps_Pagination_TotalOffers").text().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBS_PER_PAGE);
	}
	
	private Document loadPage(String url) throws IOException {
		return Jsoup.connect(url).get();
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
