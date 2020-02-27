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
 * Qapital Jobsite Parser<br>
 * URL: https://qapital.teamtailor.com/jobs
 * 
 * @author Kayumuzzaman Robin
 * @author fahim.reza
 * @author tanmoy.tushar
 * @author bm.alamin
 * @since 2019-04-01
 */
@Service
@Slf4j
public class Qapital extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.QAPITAL;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 30);
		Document document = Jsoup.connect(siteMeta.getUrl()).get();
		Elements rowList = document.select("a[class=u-primary-background-color ]");
		expectedJobCount = rowList.size();
		getSummaryPages(rowList, siteMeta);
	}

	private void getSummaryPages(Elements rowList, SiteMetaData siteMeta) throws PageScrapingInterruptedException {
		for (Element element : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + element.attr("href"));
			Elements jobTitle = element.getElementsByClass("title u-link-color u-no-hover");
			job.setTitle(jobTitle.text());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.selectFirst("div[class=body u-margin-top--medium u-primary-text-color]").text().trim());
		job.setApplicationUrl(doc.selectFirst("div[class=apply apply__bottom]>div>a").attr("href"));
		Element jobE = doc.selectFirst("h2");
		if (jobE != null) {
			String[] catLoc = jobE.text().split(" - ");
			job.setCategory(catLoc[0].trim());
			job.setLocation(catLoc[1].trim());
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
