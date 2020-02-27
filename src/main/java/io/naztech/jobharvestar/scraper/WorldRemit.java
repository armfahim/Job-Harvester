package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Worldremit job site scraper. <br>
 * URL: https://www.worldremit.com/en/careers
 * 
 * @author Asadullah Galib
 * @since 2019-03-31
 **/
@Slf4j
@Service
public class WorldRemit extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.WORLDREMIT;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.WORLDREMIT));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException, NullPointerException {
		this.baseUrl = siteMeta.getUrl().substring(0, 26);
		List<String> url = getSummaryPage(siteMeta);
		expectedJobCount = url.size();
		for (String string : url) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(string), siteMeta);
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private List<String> getSummaryPage(SiteMetaData siteMeta) throws PageScrapingInterruptedException, IOException {
		List<String> url = new ArrayList<String>();
		try {
			Document document = Jsoup.connect(siteMeta.getUrl()).get();
			Elements rowElements = document.select("a.job-item");
			for (Element element : rowElements) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				url.add(baseUrl + element.attr("href"));
			}
		} catch (IOException e) {
			log.warn("Failed to parse Site: " + getSiteName(), e);
			throw e;
		}
		return url;
	}

	private Job getJobDetails(String url) {
		Job job = new Job(url);
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Elements title = doc.select("div > h1");
			job.setTitle(title.text());
			job.setName(title.text());
			Elements location = doc.select("div.job-content-header > p");
			job.setLocation(location.text());
			Elements spec = doc.select("div.copy");
			job.setSpec(spec.get(0).text());
		} catch (IOException e) {
			log.warn("Failed to parse job details :" + job.getUrl(), e);
		}
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
