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
 * Mapfre Jobsite parser. <br>
 * URL:https://jobs.mapfre.com/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=
 *
 * @author mahmud.rana
 * @author sohid.ullah
 * @author iftekar.alam
 * @since 2019-03-04
 */
@Service
@Slf4j
public class Mapfre extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MAPFRE;
	private String baseUrl;
	private int expectedJobCount;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		String totalJob = doc.select("span[class=paginationLabel]>b").get(1).text().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		for (int i = 0; i < expectedJobCount; i += 25) {
			String url = siteMeta.getUrl() + i;
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse list of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("span[class=jobTitle visible-phone]");
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.getElementsByTag("span").get(0).getElementsByTag("a").attr("href"));
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				log.warn("Faild to parse details of " + getBaseUrl(), e);
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		String date = doc.selectFirst("p[id=job-date]").text().split(":")[1].trim();
		date = date.replace(date.substring(3, 4), date.substring(3, 4).toUpperCase());
		job.setPostedDate(parseDate(date.replace(date.substring(3, 4), date.substring(3, 4).toUpperCase()), DF));
		jobE = doc.selectFirst("span[class=jobGeoLocation]");
		job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("div[class=job]");
		job.setSpec(jobE.text().trim());
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
