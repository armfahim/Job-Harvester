package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.utils.ConnectionProvider;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * <a href="http://www.mediamath.com/careers/open-positions/">
 * Media Math Jobsite Parser</a><br>
 * 
 * @author fahim.reza
 * @since 2019-03-13
 */
@Service
@Slf4j
public class MediaMath extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MEDIAMATH;
	private String baseUrl;
	private static final int MAX_RETRY = 10;
	private int expectedJobCount;
	private Exception exception;

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

	@Autowired
	private ConnectionProvider con;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		Document document = con.getConnection(siteMeta.getUrl(), MAX_RETRY);
		Thread.sleep(TIME_5S);
		Job job = new Job();
		Elements rowElements = document.select("td.job-title");
		expectedJobCount = rowElements.size();
		for (int i = 0; i < rowElements.size() - 1; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = rowElements.get(i).child(0).attr("href");
			try {
			saveJob(getJobDetails(baseUrl + url, job), siteMeta);
			}catch(Exception e) {
				exception = e;
			}
		}
	}

	public Job getJobDetails(String url, Job job) throws InterruptedException {
		try {
			Document document = con.getConnection(url, MAX_RETRY);
			job.setUrl(baseUrl + url);
			Elements title = document.select("h2");
			Elements elements = document.select("h4 > span");
			Elements spec = document.select("div.row");
			job.setTitle(title.text());
			job.setName(job.getTitle());
			job.setCategory(elements.get(0).text());
			job.setLocation(elements.get(1).text());
			job.setPostedDate(parseDate(elements.get(2).text().trim(), DF));
			job.setSpec(spec.text().replace("Share:", ""));
		} catch (FailingHttpStatusCodeException e) {
			log.warn(SITE + " failed to connect site", e);
		} catch (NoSuchElementException e) {
			log.warn(SITE + " Failed to parse job details of " + url, e);
		}
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
