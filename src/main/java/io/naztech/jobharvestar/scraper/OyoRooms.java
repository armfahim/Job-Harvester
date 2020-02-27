package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Oyo Rooms jobs site parse <br>
 * URL: https://oyorooms.recruiterbox.com/?loc_country=Indonesia&loc_state=West%20Java#content
 * 
 * @author tanmoy.tushar
 * @since 2019-03-14
 */
@Service
@Slf4j
public class OyoRooms extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.OYO_ROOMS;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		this.baseUrl = site.getUrl().substring(0, 33);
		getSummaryPages(site, doc);
	}

	private void getSummaryPages(SiteMetaData site, Document doc) throws PageScrapingInterruptedException {
		try {
			Elements rowList = doc.select("a[class=card-title]");
			expectedJobCount = rowList.size();
			for (Element row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + row.attr("href"));
				job.setTitle(row.text());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetails(job), site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.info(getSiteName() + " Exception Occured", e);
		}
	}

	private Job getJobDetails(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Elements locE = doc.select("p[class=opening-info]>span");
			job.setLocation(locE.text());
			Element jobE = doc.select("small[class=badge-opening-meta]").first();
			job.setType(jobE.text());
			jobE = doc.select("div[class=jobdesciption]").first();
			job.setSpec(jobE.text());
			jobE = doc.select("a[class=btn btn-primary btn-lg btn-apply hidden-print]").first();
			job.setApplicationUrl(jobE.attr("href"));
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info(getSiteName() + " Failed parse job details of " + job.getUrl(), e);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
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
