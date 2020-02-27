package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.selenium.AbstractSeleniumJobLink;

/**
 * Funding Societies job parsing class<br>
 * URL: https://fsmk.bamboohr.com/jobs/
 * 
 * @author Muhammad Bin Farook
 * @author tanmoy.tushar
 * @since 2019-03-27
 */
@Service
public class FundingSocieties extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.FUNDING_SOCIETIES;
	
	@Override
	protected String getRowListPath() {
		return "//a[@class='ResAts__listing-link']";
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
		return "li[class=posInfo posInfo--employmentType]>div[class=posInfo__Value]";
	}
	@Override
	protected String getRefCssQuery() {
		return null;
	}
	@Override
	protected String getSpecCssQuery() {
		return "div[class=ResAts__page ResAts__description js-jobs-page js-jobs-description]";
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
	@Override
	public String getSiteName() {
		return SITE;
	}
	@Override
	protected String getBaseUrl() {
		return null;
	}
}
