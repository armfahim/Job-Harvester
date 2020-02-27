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
 * Ntt Data jobs site parser <br>
 * URL: https://careers-inc.nttdata.com/search/
 * 
 * @author fahim.reza
 * @since 2019-10-21
 */
@Slf4j
@Service
public class Synechron extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SYNECHRON;
	private String baseUrl;
	private static final int JOB_PER_PAGE = 6;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 25);
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		String totalJob = getTotalJob(doc);
		if (totalJob == null) {
			throw new NullPointerException("Total page number not found");
		}
		expectedJobCount = Integer.parseInt(totalJob);
		log.info("Total Job Found: " + getExpectedJob());
		int totalPage = getPageCount(totalJob, JOB_PER_PAGE);
		for (int i = 0; i <= totalPage; i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			getSummaryPage(baseUrl + "/currentvacancies?page=" + i, siteMeta);
		}

	}

	private void getSummaryPage(String url, SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Job job = new Job();
		try {
			Elements jobList = doc.select("div[class=item active] > div[class=row] > a");
			for (Element el : jobList) {
				job.setUrl(baseUrl + el.attr("href"));
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job details of : " + job.getUrl());
				}
			}

		} catch (Exception e) {
			exception = e;
			log.warn("Failed to parse summary page of : " + url);
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements title = doc.select("span[class=title]");
		Elements location = doc.select("span[class=location]");
		Element category = doc.selectFirst("span[class=group]");
		Elements spec = doc.select("div[class=row] >div[class=col-md-12 col-sm-12 col-xs-12 job-discrption]");
		job.setTitle(title.text());
		job.setName(job.getTitle());
		job.setLocation(location.text().split("and")[0].trim());
		job.setCategory(category.text());
		job.setSpec(spec.get(0).text());
		job.setPrerequisite(spec.get(1).text());
		return job;
	}

	private String getTotalJob(Document doc) {
		Elements totalJob = doc.select("div[class=col-md-3 col-sm-3 col-xs-7] > p");
		return totalJob.get(1).text().split(" ")[0].trim();
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
