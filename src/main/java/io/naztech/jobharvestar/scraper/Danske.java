package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

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
 * Danske_scrapping.<br>
 * URL: https://danskebank.com/en-uk/careers/Apply/Pages/Current-openings.aspx
 * 
 * @author farzana.islam
 * @author iftekar.alam
 * @since 2019-02-25
 */
@Slf4j
@Service
public class Danske extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DANSKE_BANK;
	private String baseUrl;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		Document doc=Jsoup.connect(siteMeta.getUrl()).get();
		Elements jobList = doc.select("table[class=gsDbgGridTable1 datagrid ]>tbody>tr");
		expectedJobCount=jobList.size();
		for (int i = 1; i < jobList.size() - 2; i++) {
			String ell = jobList.get(i).getElementsByTag("td").get(0).getElementsByTag("a").attr("onclick");
			Job job=new Job(ell.split("'")[1]);
			job.setTitle(jobList.get(i).getElementsByTag("td").get(0).getElementsByTag("a").text().trim());
			job.setName(job.getName());
			job.setLocation(jobList.get(i).getElementsByTag("td").get(1).text().trim());
			job.setCategory(jobList.get(i).getElementsByTag("td").get(2).text().trim());
			job.setDeadline(parseDate(jobList.get(i).getElementsByTag("td").get(4).text().trim(), DF));
			try {
				saveJob(getJobDetail(job), siteMeta);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of " + job.getUrl(),e);
			}
		} 
	}

	private Job getJobDetail(Job job) throws  IOException {
		Document doc=Jsoup.connect(job.getUrl()).get();
		Element refId=doc.getElementById("ctl00_m_g_5fbf1ce1_ab88_43df_970e_2b28d9257d90_lblRecruitmentKeyOutput");
		job.setReferenceId(refId.text().trim());
		Element spec = doc.getElementById("ctl00_m_g_5fbf1ce1_ab88_43df_970e_2b28d9257d90_tab1_tlb26");
		job.setSpec(spec.text().trim());
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
