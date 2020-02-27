package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
 * JP Morgan job site parser<br>
 * URL: https://jobs.jpmorganchase.com/ListJobs/All
 * 
 * @author mahmud.rana
 * @author Imtiaz Rahi
 * @author tanmoy.tushar
 * @since 2019-01-12
 */
@Slf4j
@Service
public class Jpmorgan extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.JPMORGAN_CHASE_N_CO;
	private static final int JOBS_PER_PAGE = 30;
	private String baseUrl;
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("M-dd-yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("M-d-yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	private static final DateTimeFormatter DF4 = DateTimeFormatter.ofPattern("MM-d-yyyy");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.JPMORGAN_CHASE_N_CO));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 30);
		Document doc = loadPage(siteMeta.getUrl());
		int totalPage = getTotalPageCount(doc);
		Elements rowList = doc.select("table.JobListTable > tbody > tr:not(:has(th))");
		browseJobList(rowList, siteMeta);
		for (int i = 2; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = siteMeta.getUrl() + "/Page-" + i;
			try {
				doc = loadPage(url);
				rowList = doc.select("table.JobListTable > tbody > tr:not(:has(th))");
				browseJobList(rowList, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job listing of page " + url, e);
			}
		}
	}

	private void browseJobList(Elements rowList, SiteMetaData site) throws IOException, InterruptedException {
		for (Element rowE : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Element titleE = rowE.selectFirst("td.coloriginaljobtitle > a");
			if (titleE == null)	continue;

			Job job = new Job(getBaseUrl() + titleE.attr("href"));
			job.setTitle(getJobTitle(titleE.text().trim()));
			job.setName(job.getTitle());
			job.setReferenceId(rowE.selectFirst("td.coldisplayjobid").text().trim());

			Element postEl = rowE.selectFirst("td.colpostedon");
			if (postEl != null)
				try {
					job.setPostedDate(parseDate(postEl.text().trim(), DF1, DF2, DF3, DF4));
				} catch (DateTimeParseException e) {
					log.warn("Failed to parse date value " + postEl.text() + " for job " + job.getUrl(), e);
				}
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = loadPage(job.getUrl());
		for (Element el : doc.select("div.job-subheader > div")) {
			String str = el.text();
			if (str.contains("Location:"))
				job.setLocation(str.replaceAll("Location:", "").trim());
			if (str.contains("Job Category:"))
				job.setCategory(str.replaceAll("Job Category:", "").trim());
			if (el.hasClass("referral-info"))
				job.setComment(str.trim());
		}
		job.setSpec(doc.selectFirst("div.desc").wholeText().trim());
		job.setApplicationUrl(doc.selectFirst("div.applyBtnTopDiv > a").attr("href"));
		return job;
	}

	private int getTotalPageCount(Document doc) throws IOException {
		String str = doc.select(".pager_counts").get(0).text();
		String totalJob = str.substring(str.indexOf("of") + 3).trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBS_PER_PAGE);
	}
	
	private Document loadPage(String url) throws IOException {
		return Jsoup.connect(url).timeout(TIME_1M).get();
	}
	
	private String getJobTitle(String txt) {
		if (txt.contains("–")) {
			String[] parts = txt.split("–");
			String txt1 = parts[0].trim();
			String txt2 = parts[1].trim();
			if (txt1.length() > 10) return checkingForType(txt1);
			else return checkingForType(txt2);
		}
		else if (txt.contains("-")) {
			String[] parts = txt.split("-");
			String txt1 = parts[0].trim();
			String txt2 = parts[1].trim();
			if (txt1.length() > 10) {
				return checkingForType(txt1);
			}
			else {
				if (txt2.length() > 10)	return checkingForType(txt2);
				else {
					if (parts.length > 3) return checkingForType(parts[2].trim());
					else return checkingForType(txt2);
				}
			}
		}
		else return checkingForType(txt);
	}
	
	private String checkingForType(String txt) {
		if (txt.contains(":")) txt = txt.split(Pattern.quote(":"))[0].trim();
		if (txt.contains("|")) txt = txt.split(Pattern.quote("|"))[1].trim();
		if (txt.contains("*")) txt = txt.split(Pattern.quote("*"))[0].trim();
		
		String[] types1 = new String[] {"(Market Expansion)", "(Part", "(Full"};
		for (String ob : types1) {
			if (txt.contains(ob)) txt = txt.split(Pattern.quote(ob))[0].trim();
		}
		
		String[] types2 = new String[] {"Part Time", "Full Time", "Part-Time", "Full-Time", "Part time", "Full time", "Part-time", "Full-time", "Part- Time", "Full- Time"};
		for (String ob : types2) {
			if (txt.contains(ob)) txt = txt.replace(ob, "").trim();
		}
		
		return txt;
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
