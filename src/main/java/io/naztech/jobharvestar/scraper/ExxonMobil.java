package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;
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
 * Exxon Mobil job site parser. <br>
 * URL:
 * https://jobs.exxonmobil.com/search/?createNewAlert=false&q=&locationsearch=
 * 
 * @author Armaan Seraj Choudhury
 * @author tanmoy.tushar
 * @author fahim.reza
 * @since 2019-03-07
 */
@Service
@Slf4j
public class ExxonMobil extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.EXXON_MOBIL_CORPORATION;
	private String baseUrl;
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private int expectedJobCount;
	private Exception exception;
	private int maxRetry=0;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(ShortName.EXXON_MOBIL_CORPORATION));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 27);
		int totalJob = getTotalJobs(siteMeta);
		for (int i = 0; i < totalJob; i += 25) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			String url = siteMeta.getUrl() + "&startrow=" + i;
			try {
				browseJobList(siteMeta, url);
			} catch (Exception e) {
				log.warn("Failed to parse job list of " + url, e);
			}
		}
	}

	private void browseJobList(SiteMetaData siteMeta, String url) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("div[class=jobdetail-phone visible-phone]");
		for (Element element : rowList) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + element.getElementsByTag("a").attr("href"));
			job.setTitle(element.getElementsByTag("a").text());
			job.setName(job.getTitle());
			job.setLocation(element.getElementsByClass("jobLocation").text());
			job.setPostedDate(parseDate(element.getElementsByClass("jobDate visible-phone").text(), DF1, DF2));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					saveJob(getJobDetail(job), siteMeta);
				} else {
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.getElementsByClass("btn btn-primary btn-large btn-lg dropdown-toggle").get(0);
		if (jobE != null)
			job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		jobE = doc.selectFirst("span[class=jobdescription]");
		job.setSpec(jobE.text());
		return job;
	}

	private int getTotalJobs(SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Element el = doc.selectFirst("span.paginationLabel");
		String totalJob = el.text().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getExpectedJob();
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
