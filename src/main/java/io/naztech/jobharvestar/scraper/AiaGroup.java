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
 * AIA Group
 * 
 * @author tohedul.islum
 * @since 2019-02-10
 * 
 * URL : https://careers.aia.com/search
 * @ISSUE RESOLVER Armaan Seraj Choudhury
 * @since 2019-3-10
 */
@Service
@Slf4j
public class AiaGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AIA_GROUP;
	private String baseUrl;
	private static final String url = "https://careers.aia.com";
	private static final int JOBLIST_OFFSET = 25;
	private static final DateTimeFormatter DF5 = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private int expectedJobCount = 0;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
			startSiteScrapping(getSiteMetaData(ShortName.AIA_GROUP));
		
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		int totalPages = getTotalPages(siteMeta);
		for (int i = 1; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Thread.sleep(5000);
			if (log.isDebugEnabled()) log.debug("Scrapping on Page " + i);
			String nextPage = this.baseUrl + "/search&startrow=" + (i* JOBLIST_OFFSET - JOBLIST_OFFSET);
			getSummaryPage(siteMeta, nextPage);
		}
	}

	private int getTotalPages(SiteMetaData siteMeta) throws IOException {
		try {
			Document doc = Jsoup.connect(siteMeta.getUrl()).get();
			Element el = doc.selectFirst("span.paginationLabel");
			String totalJob = el.text().split("of")[1].trim();
			expectedJobCount=Integer.parseInt(totalJob);
			return getPageCount(totalJob, JOBLIST_OFFSET);
		} catch (IOException e) {
			log.error(SITE + " failed to parse total page count" + e);
			throw e;
		}
	}

	private void getSummaryPage(SiteMetaData siteMeta, String next) throws InterruptedException{
		try {
			Elements list = Jsoup.connect(next).get().select("div.searchResultsShell > table > tbody > tr");
			for (Element el : list) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				String joburl = url + el.child(0).child(0).child(0).attr("href");
				Job job = new Job();
				job.setUrl(joburl);
				job.setTitle(el.child(0).child(0).child(0).text());
				job.setName(job.getTitle());
				job.setLocation(el.child(0).child(1).child(1).child(0).text());
				job.setCategory(el.child(3).child(0).text());	
				job.setPostedDate(parseDate(el.child(4).child(0).text(),DF5));
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Exception in getSummaryPage: " + e);
		}
	}

	private Job getJobDetail(Job job) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();			
		Element descE = doc.selectFirst("div[class=job]");
		job.setSpec(descE.text());
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
