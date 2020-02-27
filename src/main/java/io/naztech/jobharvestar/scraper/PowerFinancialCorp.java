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
 * Power Financial Corp job site parser. <br>
 * URL: https://www.powerfinancial.com/en/other/careers/
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-02-12
 */
@Service
@Slf4j
public class PowerFinancialCorp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.POWER_FINANCIAL_CORP;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.POWER_FINANCIAL_CORP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 30);
		Document doc = loadPage(site.getUrl());
		Elements rowList = doc.select("div[class=col-xs-12 col-sm-8 wrapper_job_title]>a");
		expectedJobCount=rowList.size();
		browseJobList(rowList, site);
	}
	
	private void browseJobList(Elements rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.attr("href"));
			String title = el.text().trim();
			if (title.contains(" - ")) {
				String[] parts = title.split(" - ");
				job.setTitle(parts[0].trim());
				job.setPostedDate(parseDate(parts[1].trim(), DF));
			}
			else job.setTitle(title);
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		job.setSpec(loadPage(job.getUrl()).selectFirst("div[class=content_box blue_border_top]").text().trim());
		return job;
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
