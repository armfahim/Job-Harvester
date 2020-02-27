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
 * Tokyo Century Leasing (Singapore) Pte Ltd. <br>
 * URL: https://www.jobstreet.com.sg/en/job-search/jobs-at-tokyo-century-leasing-singapore-pte-ltd
 * 
 * @author BM Al-Amin
 * @since 2019-02-25
 */
@Slf4j
@Service
public class TokyoCenturyCorp extends AbstractScraper implements Scrapper {
	private static String baseUrl;
	private static final String JOBSITE = ShortName.TOKYO_CENTURY_CORP;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(JOBSITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc;
		try {
			doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		} catch (Exception e) {
			doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		}
		Elements rowList = doc.select("a[id=position_title_1]");
		expectedJobCount=rowList.size();
		for(int i = 0; i < rowList.size(); i++) {
			Job job=new Job(rowList.get(i).attr("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse details for "+job.getUrl(),e);
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[id=company_registration_number]");
		job.setReferenceId(jobE.text().trim());
		jobE = doc.selectFirst("p[id=company_industry]");
		job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("span[id=single_work_location]");
		job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("div[id=job_description]>ul");
		job.setSpec(jobE.text().trim());
		jobE = doc.select("div[id=job_description]>ul").get(1);
		job.setPrerequisite(jobE.text().trim());
		return job;
	}

	@Override
	public String getSiteName() {
		return JOBSITE;
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