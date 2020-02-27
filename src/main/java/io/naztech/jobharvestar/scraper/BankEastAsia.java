package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;
/**
 * Bank EastAsia Jobsite Parser<br>
 * URL: https://careers.hkbea.com/psp/hcmprd/EMPLOYEE/HRMS/c/HRS_HRAM.HRS_APP_SCHJOB.GBL?FOCUS=Applicant&FolderPath=PORTAL_ROOT_OBJECT.HC_HRS_CE_GBL2&IsFolder=false&IgnoreParamTempl=FolderPath%252cIsFolder
 * 
 * @author Rahat Ahmad
 * @since 2019-03-05
 */
@Slf4j
@Service
public class BankEastAsia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANK_EAST_ASIA;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 26);
		HtmlPage page = client.getPage(siteMeta.getUrl());
		page = (HtmlPage) page.getFrameByName("TargetContent").getEnclosedPage();
		List<HtmlElement> jobListE = page.getByXPath("//table[@class='PSLEVEL1GRID']/tbody/tr[@valign = 'center']");
		expectedJobCount = jobListE.size();
		HtmlElement firstJobE = (HtmlElement) jobListE.get(0).getByXPath("//a[@id = 'POSTINGTITLE$0']").get(0);
		page = firstJobE.click();
		Thread.sleep(TIME_5S);
		for (int i = 0; i < jobListE.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			try {
			saveJob(getJobDetails(page, siteMeta), siteMeta);
			}catch(Exception e) {
				exception =e;
			}
			/*
			 * On the last job, next job button will be disabled so condition not added on
			 * loop. If it is use on loop condition, last job data wouldn't fetch.
			 */
			if (i == jobListE.size() - 1) break;
			page = page.getElementById("HRS_SCH_WRK2_HRS_NEXT_JOB_LNK").click();
			Thread.sleep(TIME_5S);
		}
	}

	private Job getJobDetails(HtmlPage page, SiteMetaData siteMeta) {
		Job job = new Job();
		try {
			HtmlElement title = (HtmlElement) page.getByXPath("//div[@id ='win0divHRS_SCH_WRK2_POSTING_TITLE']/span").get(0);
			job.setTitle(title.asText());
			job.setName(job.getTitle());
			HtmlElement location = (HtmlElement) page.getByXPath("//div[@id ='win0divHRS_SCH_WRK_HRS_DESCRLONG']/span").get(0);
			job.setLocation(location.asText());
			HtmlElement jobRefId = (HtmlElement) page.getByXPath("//div[@id ='win0divHRS_SCH_WRK2_HRS_JOB_OPENING_ID']/span").get(0);
			job.setReferenceId(jobRefId.asText());
			HtmlElement jobType = (HtmlElement) page.getByXPath("//div[@id ='win0divHRS_SCH_WRK_HRS_FULL_PART_TIME']/span").get(0);
			job.setType(jobType.asText());
			job.setSpec(page.getElementById("ACE_HRS_SCH_PSTDSC$0").asText());
			job.setUrl(getBaseUrl() + getJobHash(job));
		} catch (ElementNotFoundException e) {
			log.warn("Failed to parse job details of " + job.getTitle(), e);
		}
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
