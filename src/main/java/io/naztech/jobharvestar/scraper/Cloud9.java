package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Cloud9 job site scraper. <br>
 * URL: https://cloud9.recruiterbox.com/
 * 
 * @author muhammad tarek
 * @author iftekar.alam
 * @since 2019-03-25
 */
@Service
public class Cloud9 extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CLOUD9;
	private String baseUrl;
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		String baseUrl = site.getUrl().substring(0, 31);
		Elements rowList = doc.select("div[class=col-md-6]>a");
		expectedJobCount = rowList.size();
		for (Element tr : rowList) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(baseUrl+tr.attr("href"));
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element title = doc.selectFirst("h1[class=jobtitle meta-job-detail-title]");
			job.setTitle(title.text());
			Element location = doc.selectFirst("p[class=opening-info]");
			job.setLocation(location.text());
			Element spec = doc.selectFirst("div[class=jobdesciption]");
			job.setSpec(spec.text());
			Element applicationUrl = doc.selectFirst("a[class=btn btn-primary btn-lg btn-apply hidden-print]");
			job.setApplicationUrl(applicationUrl.attr("href"));
		    return job;
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
