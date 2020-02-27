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
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * AeonFinancialServiceHongKong job site scrapper.<br>
 * URL: https://www.aeon.com.hk/en/html/corporate-info/careers.html
 * 
 * @author Alif Choyon
 * @since 2019-03-03
 */
@Slf4j
@Service
public class AeonFinancialServiceHongKong extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.AEON_HONG_KONG;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		getSummaryPages(site);
	}

	public void getSummaryPages(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		try {
			Document doc = Jsoup.connect(site.getUrl()).get();
			Elements titleListE = doc.select("div[class=h5title]");
			expectedJobCount = titleListE.size();
			for (int i = 0; i < titleListE.size() - 1; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(titleListE.get(i)), site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse job details" + site.getUrl(), e);
			throw e;
		}
	}

	public Job getJobDetails(Element el) throws PageScrapingInterruptedException {
		Job job = new Job();
		job.setTitle(el.text());
		job.setName(job.getTitle());
		job.setUrl(getJobHash(job));
		Element sibling = el.nextElementSibling();
		String spec = "";
		do {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			if (!sibling.hasClass("titleBottom") && sibling.hasText())
				spec += sibling.text();
			sibling = sibling.nextElementSibling();
		} while (!sibling.hasClass("h5title"));
		job.setSpec(spec);
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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