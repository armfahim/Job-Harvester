package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
 * ING Group jobs site parser. <br>
 * URL: https://www.ing.jobs/Global/Careers/Job-opportunities.htm?start=0
 * 
 * @author mahmud.rana
 * @author tanmoy.tushar
 * @since 2019-01-20
 */
@Service
@Slf4j
public class IngGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ING_GROEP;
	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String baseUrl;
	private SiteMetaData siteMeta;
	private int expectedJobCount = 0;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(siteMeta = getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 20);
		int totalPages = getTotalPageCount(siteMeta.getUrl() + 0);
		for (int i = 0; i < totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = siteMeta.getUrl() + i * 50;
			try {
				browseJobList(url, i + 1);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(String url, int i) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements els = doc.select("div.careers-search-result");
		for (Element element : els) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + element.child(0).child(0).attr("href"));
			job.setTitle(element.child(0).child(0).text());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), this.siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Element element = doc.selectFirst("p.light-text");
			if (element != null) {
				String[] parts = element.text().split("\\|");
				if (parts.length > 5) {
					try {
						job.setPostedDate(LocalDate.parse(parts[parts.length - 2].trim(), DF));
						job.setCategory(parts[1].trim());
						job.setLocation(parts[3].trim());
						job.setReferenceId(parts[parts.length - 1].trim());
					} catch (DateTimeParseException e) {
						job.setLocation(parts[parts.length - 4].trim());
						job.setCategory(parts[0].trim());
						job.setReferenceId(parts[parts.length - 2].trim());
					}
				} else {
					job.setReferenceId(parts[parts.length - 1].trim());
					try {
						job.setPostedDate(LocalDate.parse(parts[parts.length - 2].trim(), DF));
						job.setLocation(parts[parts.length - 3].trim());
					} catch (DateTimeParseException e) {
						job.setLocation(parts[parts.length - 2].trim());
						job.setCategory(parts[0].trim());
					}
				}
			}
			element = doc.selectFirst("div[class=vacancy-content]");
			if (element != null)
				job.setSpec(element.text().trim());
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse detail page of " + job.getUrl(), e);
			return null;
		}
	}

	private int getTotalPageCount(String url) throws IOException {
		try {
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Element el = doc.selectFirst("div[class=careers-search-filters]");
			String totalJob = el.nextElementSibling().text().split("of")[1].trim().split(" ")[0].trim();
			expectedJobCount = Integer.parseInt(totalJob);
			return getPageCount(totalJob, 50);
		} catch (IOException e) {
			log.error("Failed to parse total job, site exiting....", e);
			throw e;
		}
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
