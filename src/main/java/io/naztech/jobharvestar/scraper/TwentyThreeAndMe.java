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
 * 23AndME job site parse<br>
 * URL: https://www.23andme.com/en-int/careers/
 * 
 * @author rafayet.hossain
 * @since 2019-04-17
 */
@Slf4j
@Service
public class TwentyThreeAndMe extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TWENTY_THREE_AND_ME;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		browseJobList(siteMeta);
	}

	private void browseJobList(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		try {
			Job job = new Job();
			Document document = Jsoup.connect(site.getUrl()).get();
			Elements el = document.getElementsByClass("careers-nested-drawer-a");
			expectedJobCount = el.size();
			for (int i = 0; i < el.size(); i++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				job.setTitle(el.get(i).text());
				job.setName(job.getTitle());
				job.setUrl(baseUrl + el.get(i).attr("href"));
				try {
					saveJob(getJobDetails(job), site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse Job List " + e);
			throw e;
		}
	}

	private Job getJobDetails(Job job) {
		try {
			Document document = Jsoup.connect(job.getUrl()).get();
			Element location = document.select("div.careers-detail-container>p").get(0);
			Elements description = document.select("div.careers-detail-container>p");
			Elements applicationUrl = document.select("a[class=button mod-large ]");
			job.setLocation(location.text());
			job.setSpec(description.text());
			job.setApplicationUrl(baseUrl + applicationUrl.attr("href"));
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
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
