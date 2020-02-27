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
 * Capgemini job site parser. <br>
 * URL: https://www.capgemini.com/careers/job-search/?show_posts=100
 * 
 * @author tanmoy.tushar
 * @since 2019-10-16
 */
@Slf4j
@Service
public class Capgemini extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CAPGEMINI;
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final String ROW_LIST = "h3[class=card_default__title]>a";
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 45);
		Document doc = loadPage(site.getUrl());
		int totalPage = getTotalPage(doc);
		expectedJobCount = totalPage * 100;
		Elements rowList = doc.select(ROW_LIST);
		browseJobList(rowList, site);
		for (int i = 2; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = baseUrl + "page/" + i + "/?show_posts=100";
			try {
				doc = loadPage(url);
				rowList = doc.select(ROW_LIST);
				browseJobList(rowList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = el.attr("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = loadPage(job.getUrl());
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=article-text]").text().trim());
		Element jobE = doc.selectFirst("a[class=section__button section__button--blue]");
		job.setApplicationUrl(jobE.attr("href"));
		Elements jobInfo = doc.select("p[class=careers__jobinfo__header]");
		for (Element el : jobInfo) {
			if (el.text().contains("Ref:"))	job.setReferenceId(el.nextElementSibling().text().trim());
			if (el.text().contains("Posted")) job.setPostedDate(parseDate(el.nextElementSibling().text().trim(), DF1, DF2));
			if (el.text().contains("Location:")) job.setLocation(el.nextElementSibling().text().trim());
			if (el.text().contains("Department:")) job.setCategory(el.nextElementSibling().text().trim());
		}
		return job;
	}

	private int getTotalPage(Document doc) {
		Element el = doc.selectFirst("div[class=pagination__current-page]");
		return Integer.parseInt(el.text().split("of")[1].trim());
	}

	private Document loadPage(String url) throws IOException {
		return Jsoup.connect(url).get();
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
