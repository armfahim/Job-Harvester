package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * OpenDoorLabs job parsing class.<br>
 * URL: https://www.opendoor.com/jobs
 * 
 * @author Muhammad Bin Farook
 * @since: 2019-03-13
 * 
 * @author tanmoy.tushar
 * @since 2016-04-16
 */
@Service
public class OpenDoorLabs extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.OPENDOOR_LABS;

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
		return "//p[@class='position']/a";
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
		return "div[class=sort-by-time posting-category medium-category-label]";
	}

	@Override
	protected String getCategoryCssQuery() {
		return "div[class=sort-by-team posting-category medium-category-label]";
	}

	@Override
	protected String getJobTypeCssQuery() {
		return "div[class=sort-by-commitment posting-category medium-category-label]";
	}

	@Override
	protected String getRefCssQuery() {
		return null;
	}

	@Override
	protected String getSpecCssQuery() {
		return "div[class=section-wrapper page-full-width]";
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
		return "a[class=postings-btn template-btn-submit cerulean]";
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}
