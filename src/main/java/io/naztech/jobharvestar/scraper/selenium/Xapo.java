package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Xapo jobs site parse <br>
 * url https://xapo.com/careers/#opportunities
 * 
 * @author sohid.ullah
 * @since 2019-03-31
 * 
 * @author tanmoy.tushar
 * @since 2019-04-10
 */
@Service
public class Xapo extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.XAPO;
	
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
		return null;
	}

	@Override
	protected String getTitleCssQuery() {
		return "h2";
	}

	@Override
	protected String getLocationCssQuery() {
		return "li[class=posInfo]>div[class=posInfo__Value]";
	}

	@Override
	protected String getCategoryCssQuery() {
		return "li[class=posInfo posInfo--department]>div[class=posInfo__Value]";
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
		return "div[class=col-xs-12]";
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
