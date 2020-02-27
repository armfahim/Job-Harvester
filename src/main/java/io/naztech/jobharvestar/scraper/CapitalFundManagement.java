package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Capital Fund Management job parsing class<br>
 * URL: https://cfmcareers.fr/?page=advertisement&sort=&p=
 * 
 * @author Armaan Seraj Choudhury
 * @author iftekar.alam
 * @author fahim.reza
 * @since 2019-03-10
 */
@Service
@Slf4j
public class CapitalFundManagement extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CAPITAL_FUND_MANAGEMENT;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl=site.getUrl().substring(0, 22);
		int totalJob=getTotalJob(site);
		for (int i = 1; i <= totalJob; i++) {
			String url=site.getUrl()+i;
			try {
				getSummaryPages(url,site);
			} catch (Exception e) {
				log.warn("Failed to parse job list of "+url,e);
			}
		}
	}
	
	private void getSummaryPages(String url,SiteMetaData site) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("table[class=tablelist]>tbody>tr");
		for (int i = 1; i <=rowList.size()-1; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl()+rowList.get(i).getElementsByTag("td").get(0).getElementsByTag("a").attr("href"));	
			job.setTitle(rowList.get(i).getElementsByTag("td").get(0).getElementsByTag("a").text().trim());
			job.setName(job.getTitle());
			job.setType(rowList.get(i).getElementsByTag("td").get(2).text().trim());
			job.setLocation(rowList.get(i).getElementsByTag("td").get(3).text().trim());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				log.warn("Faild to parse details of " + getBaseUrl(), e);
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element spec = doc.selectFirst("div[id=advert]");
		job.setSpec(spec.text().trim());
		Element appUrl=doc.select("a[class=btn btn-default btn-sm]").get(2);
		job.setApplicationUrl(getBaseUrl()+appUrl.getElementsByTag("a").attr("href"));
		return job;
	}
	
	private int getTotalJob(SiteMetaData site) throws IOException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		Element totalJob = doc.selectFirst("td[class=navigation_index]");
		expectedJobCount=Integer.parseInt(totalJob.text().split("/")[1].trim());
		return getPageCount(totalJob.text().split("/")[1].trim(), 10);
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
