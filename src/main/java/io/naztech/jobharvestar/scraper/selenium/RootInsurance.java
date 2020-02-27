package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Root Insurance job site scraper. <br>
 * URL: https://www.joinroot.com/careers
 * 
 * @author a.s.m. tarek
 * @since 2019-03-12
 * 
 * @author tanmoy.tushar
 * @since 2019-04-16
 */
@Service
public class RootInsurance extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.ROOT_INSURANCE;

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
		return "//a[@class='rJobPostingListItem__cta']";
	}

	@Override
	protected String getFirstPageCatPath() {
		return null;
	}

	@Override
	protected String getFirstPageLocPath() {
		return "//p[@class='rJobPostingListItem__location']";
	}

	@Override
	protected String getTitleCssQuery() {
		return "span[class=careerTitle__position]";
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
		return "ul[class=rCareersCareer__list list]";
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
		return "a[class=rButton rCareersCareer__applyButton -orange rButton--a]";
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}