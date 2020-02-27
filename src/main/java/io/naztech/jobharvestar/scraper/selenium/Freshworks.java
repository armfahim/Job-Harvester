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
import lombok.extern.slf4j.Slf4j;

/**
 * Fresh Works job site parser. <br>
 * URL: https://careers.freshworks.com/jobs
 * 
 * @author tohedul.islum
 * @since 2019-03-12
 * 
 * @author tanmoy.tushar
 * @since 2019-04-23
 */
@Service
@Slf4j
public class Freshworks extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.FRESHWORKS;

	@Override
	protected Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h2");
			job.setTitle(jobE.text().trim());
			job.setName(job.getTitle());
			Elements jobInfoL = doc.select("div[class=stick-hide-in-mobile  text-color job-other-info]>span");
			if (jobInfoL.size() == 2) {
				job.setLocation(jobInfoL.get(0).text().trim());
				job.setType(jobInfoL.get(1).text().trim());
			}
			jobE = doc.selectFirst("div[class=job-details-content content]");
			job.setSpec(jobE.text().trim());
			jobE = doc.selectFirst("a[class=link-back]");
			job.setCategory(jobE.text().trim());
			job.setApplicationUrl(job.getUrl() + "#applicant-form");
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
		return "//li[@class='heading']/a";
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
