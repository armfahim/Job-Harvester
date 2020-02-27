package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Medallia Jobsite Parser. <br>
 * URL: https://jobs.medallia.com/
 * 
 * @author Fahim Reza
 * @since 2019-03-12
 * 
 * @author tanmoy.tushar
 * @since 2019-04-16
 */
@Service
public class Medallia extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.MEDALLIA;

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
		return "//div[@id='medallia_jobs']/ul/li/a";
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
		return "h2";
	}

	@Override
	protected String getLocationCssQuery() {
		return "span[class=j-location]";
	}

	@Override
	protected String getCategoryCssQuery() {
		return "span[class=j-dept]";
	}

	@Override
	protected String getJobTypeCssQuery() {
		return "span[class=j-commitment]";
	}

	@Override
	protected String getRefCssQuery() {
		return null;
	}

	@Override
	protected String getSpecCssQuery() {
		return "div[class=j-description]";
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
		return "a[class=j-apply cta cta-blue]";
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}
