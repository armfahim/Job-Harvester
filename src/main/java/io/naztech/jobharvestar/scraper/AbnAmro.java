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
 * Abn Amro job site scrapper. <br>
 * Url: https://www.abnamro.com/en/careers/vacancies/index.html
 * 
 * @author naym.hossain
 * @since 2019-01-24
 */
@Slf4j
@Service
public class AbnAmro extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ABN_AMRO_GROUP;
	private String baseUrl;
	private static final String HEADURL = "/careers/vacancies/index.html?page=";
	private static final String TAILURL = "&lang=en";
	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.ABN_AMRO_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 26);
		Document docTotalPages = Jsoup.connect(siteMeta.getUrl()).get();
		Element elTotalJob = docTotalPages.selectFirst("div.result-list>p");
		expectedJobCount = Integer.parseInt(elTotalJob.text().substring(0, 3));
		int totalPage = getPageCount(elTotalJob.text().substring(0, 3), 10);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPages(getBaseUrl() + HEADURL + i + TAILURL, siteMeta);
		}
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException {
		try {
			Document docSummary = Jsoup.connect(url).get();
			Elements elSummary = docSummary.select("div.result-list > article");
			for (Element el : elSummary) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + el.child(0).attr("href").substring(1));
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch(Exception e) {
					exception = e;
				}
			}
		} catch (NullPointerException | IOException e) {
			log.warn("Failed to Parse Summary Page");
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("span[itemprop=address]");
		job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("li[class=expertise]>a");
		job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("li[class=number]>span").nextElementSibling();
		job.setReferenceId(jobE.text().trim());
		jobE = doc.selectFirst("span[itemprop=datePosted]");
		job.setPostedDate(parseDate(jobE.text().trim(), DF));
		jobE = doc.selectFirst("div[itemprop=description]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("a[class=btn btn-primary]");
		job.setApplicationUrl(getBaseUrl() + jobE.attr("href").substring(1)); 
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
