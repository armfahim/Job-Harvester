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
 * Atos Jobsite Parser<br>
 * URL: https://jobs.atos.net/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=
 * 
 * @author iftekar.alam
 * @since 2019-10-16
 */
@Service
@Slf4j
public class Atos extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ATOS;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM d, yyyy");

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.ATOS));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		this.baseUrl = siteMeta.getUrl().substring(0, 21);
		Element totaljob = doc.select("span[class=paginationLabel]>b").get(1);
		expectedJobCount = Integer.parseInt(totaljob.text().trim());
		for (int i = 0; i < expectedJobCount; i=i+50) {
			String url=siteMeta.getUrl()+i;
			try {
				browseJobList(url,siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse list of "+ url,e);
			}
		}
	}

	private void browseJobList(String url,SiteMetaData siteMeta) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("tr[class=data-row clickable]");
		for (Element el : rowList) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(getBaseUrl()+el.getElementsByTag("td").get(0).getElementsByTag("span").get(0).getElementsByTag("a").attr("href"));
			job.setTitle(el.getElementsByTag("td").get(0).getElementsByTag("span").get(0).text().trim());
			job.setName(job.getTitle());
			job.setCategory(el.getElementsByTag("td").get(1).getElementsByTag("span").text().trim());
			job.setLocation(el.getElementsByTag("td").get(2).getElementsByTag("span").text().trim());
			job.setPostedDate(parseDate(el.getElementsByTag("td").get(3).getElementsByTag("span").text().trim(), DF,DF1));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}
	
	
	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element spec = doc.selectFirst("div[class=job]");
		job.setSpec(spec.text().trim());
		Element rowList = doc.selectFirst("a[class=btn btn-primary btn-large btn-lg apply dialogApplyBtn ]");
		job.setApplicationUrl(getBaseUrl()+rowList.attr("href"));
		
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
