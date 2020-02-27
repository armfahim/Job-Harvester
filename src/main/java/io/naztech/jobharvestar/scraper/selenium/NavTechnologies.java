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
 * Nav Technologies job site parser. <br>
 * URL: https://www.nav.com/company/careers/#openings
 * 
 * @author Alif Choyon
 * @since 2019-04-02
 * 
 * @author tanmoy.tushar
 * @since 2019-04-16
 */
@Service
public class NavTechnologies extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.NAV_TECHNOLOGIES;

	@Override
	protected Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h2");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("div[class=col-xs-12]");
			job.setSpec(jobE.text());
			Elements jobInfo = doc.select("span[class=i2b]>strong");
			if (jobInfo.size() > 2) {
				job.setCategory(jobInfo.get(0).text());
				job.setType(jobInfo.get(1).text());
			}
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
		return "//li[@class='BambooHR-ATS-Jobs-Item']/a";
	}

	@Override
	protected String getFirstPageCatPath() {
		return null;
	}

	@Override
	protected String getFirstPageLocPath() {
		return "//span[@class='BambooHR-ATS-Location']";
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
