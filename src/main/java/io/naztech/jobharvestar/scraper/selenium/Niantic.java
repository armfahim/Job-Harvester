package io.naztech.jobharvestar.scraper.selenium;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Niantic job site parser. <br>
 * URL: https://nianticlabs.com/jobs/
 * 
 * @author Armaan Choudhury
 * @since 2019-03-13
 * 
 * @author tanmoy.tushar
 * @since 2019-04-16
 */
@Service
public class Niantic extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.NIANTIC;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	protected Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Element jobE = doc.selectFirst("div[class=job-details__title]");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("div[class=grid__item grid__item--name]");
			job.setLocation(jobE.text());
			jobE = doc.selectFirst("div[class=job-details__body]");
			job.setSpec(jobE.text());
			Elements jobInfo = doc.select("div[class=job-details__eyebrow]>span");
			if (jobInfo.size() == 2) {
				job.setCategory(jobInfo.get(0).text());
				job.setType(jobInfo.get(1).text());
			}
			jobE = doc.selectFirst("a[class=button button--push]");
			job.setApplicationUrl(jobE.attr("href"));
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
			return null;
		}
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

	@Override
	protected String getRowListPath() {
		return "//a[@class='jobs-search__jobs__group__job__inner']";
	}

	@Override
	protected String getFirstPageCatPath() {
		return null;
	}

	@Override
	protected String getFirstPageLocPath() {
		return null;
	}

	@Override
	protected String getTitleCssQuery() {
		return null;
	}

	@Override
	protected String getLocationCssQuery() {
		return null;
	}

	@Override
	protected String getCategoryCssQuery() {
		return null;
	}

	@Override
	protected String getJobTypeCssQuery() {
		return null;
	}

	@Override
	protected String getRefCssQuery() {
		return null;
	}

	@Override
	protected String getSpecCssQuery() {
		return null;
	}

	@Override
	protected String getPreReqCssQuery() {
		return null;
	}

	@Override
	protected String getPostedDateCssQuery() {
		return null;
	}

	@Override
	protected String getApplyUrlCssQuery() {
		return null;
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}