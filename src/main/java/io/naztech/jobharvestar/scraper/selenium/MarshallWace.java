package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Marshall Wace Jobsite Parser.<br>
 * URL: https://www.mwam.com/roles
 * 
 * @author Fahim Reza
 * @since 2019-03-05
 * 
 * @author tanmoy.tushar
 * @since 201-04-16
 */
@Service
public class MarshallWace extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.MARSHALL_WACE;

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
		return "//li[@class='Vacancies__list__item']/a";
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
		return "div[class=Columns__copy]";
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
		return "div[class=Columns__buttons]>a";
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}
