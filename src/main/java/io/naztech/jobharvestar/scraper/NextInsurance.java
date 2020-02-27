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
 * Next Insurance job site parser<br>
 * URL: https://www.next-insurance.com/careers/
 * 
 * @author rafayet.hossain
 * @author iftekar.alam
 * @since 2019-04-02
 */
@Slf4j
@Service
public class NextInsurance extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NEXT_INSURANCE;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, PageScrapingInterruptedException {
		Document doc=Jsoup.connect(site.getUrl()).get();
		Elements jobList = doc.select("div[class=position]");
		expectedJobCount = jobList.size();
		for (Element el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(el.select("h2>a").attr("href"));
			job.setTitle(el.select("h2>a").text().trim());
			job.setName(job.getTitle());
			if(el.getElementsByTag("p").get(1).getElementsByTag("a").size()==2) {
				job.setLocation(el.getElementsByTag("p").get(1).getElementsByTag("a").get(1).text().trim());
				job.setCategory(el.getElementsByTag("p").get(1).getElementsByTag("a").get(0).text().trim());
			} else job.setLocation(el.getElementsByTag("p").get(1).getElementsByTag("a").get(0).text().trim());
			try {
				saveJob(getJobDetails(job), site);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+job.getUrl(),e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException  {
		Document doc=Jsoup.connect(job.getUrl()).get();
		Element description = doc.selectFirst("div[class=position-content]");
		job.setSpec(description.text().trim());
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
