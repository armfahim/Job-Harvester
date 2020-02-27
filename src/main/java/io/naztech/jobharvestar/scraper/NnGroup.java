package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

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
 * NN Group Job site Parser. <br>
 * URL: https://nn-careers.com/jobs/?filter-searchphrase=
 * 
 * @author Armaan Seraj Choudhury
 * @author tanmoy.tushar
 * @since 2019-02-14
 */
@Slf4j
@Service
public class NnGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NN_GROUP;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMMM yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 28);
		Document doc = loadPage(site.getUrl());
		int totalPage = getTotalPage(doc);
		Elements rowList = doc.select("header[class=equal-title]>h2>a");
		Elements infoList = doc.select("ul[class=meta equal-meta]");
		browseJobList(rowList, infoList, site);
		for (int i = 2; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "page/" + i + "/?filter-searchphrase";
			try {
				doc = loadPage(url);
				rowList = doc.select("header[class=equal-title]>h2>a");
				infoList = doc.select("ul[class=meta equal-meta]");
				browseJobList(rowList, infoList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, Elements infoList, SiteMetaData site) throws InterruptedException {
		for (int i = 0; i < rowList.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(rowList.get(i).attr("href"));
			job.setTitle(rowList.get(i).text().trim());
			job.setName(job.getTitle());
			job.setPostedDate(parseDate(infoList.get(i).getElementsByClass("date").text(), DF));
			job.setCategory(infoList.get(i).getElementsByClass("company-departments").text().trim());
			job.setLocation(infoList.get(i).getElementsByClass("location").text().trim());
			job.setReferenceId(infoList.get(i).getElementsByClass("job-id").text().trim().split(":")[1].trim());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = loadPage(job.getUrl());
		Element jobE = doc.getElementById("content").selectFirst("section[class=post-content]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("a[class=btn apply]");
		job.setApplicationUrl(jobE.attr("href"));
		return job;
	}

	private int getTotalPage(Document doc) {
		Element el = doc.getElementById("searchfilter").getElementsByTag("legend").get(0);
		String totalJob = el.text().trim().split(Pattern.quote("("))[1].split(Pattern.quote(")"))[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 9);
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