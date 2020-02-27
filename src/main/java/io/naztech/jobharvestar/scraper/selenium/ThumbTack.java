package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * ThumbTack job parsing class <br>
 * URL: https://www.thumbtack.com/careers/#jobs-all-roles
 * 
 * @author Muhammad Bin Farook
 * @since: 2019-03-14
 * 
 * @author tanmoy.tushar
 * @since 2019-04-16
 */
@Service
public class ThumbTack extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.THUMBTACK;

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
		return "//div[@class='ng-scope']/a";
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
		return "div[class=location]";
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
		return "div[id=content]";
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
