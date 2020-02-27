package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Prospa Jobsite Parser. <br>
 * URL: https://www.prospa.com/about-us/careers#open-jobs
 * 
 * @author Muhammad Bin Farook
 * @since 2019-03-27
 * 
 * @author tanmoy.tushar
 * @since 2019-04-09
 */
@Service
public class Prospa extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.POSPA;

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
		return "//table[@class='prospa-jobs-listing']/tbody/tr/td/a";
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
		return "h1";
	}

	@Override
	protected String getLocationCssQuery() {
		return "h3";
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
		return "div[class=job-content]";
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
