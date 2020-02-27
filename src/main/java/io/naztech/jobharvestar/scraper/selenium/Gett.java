package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Gett job site parsing class. <br>
 * URL: https://gett.com/careers/
 * 
 * @author tanmoy.tushar
 * @since 2019-03-27
 */
@Service
public class Gett extends AbstractSeleniumJobList {
	private static final String SITE = ShortName.GETT;
	
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
		return "//ul[@id='jobs']/li/a";
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
	protected String getTitleXPath() {
		return "//h2[@class='title']";
	}

	@Override
	protected String getLocationXPath() {
		return "//div[@class='alignleft location']";
	}

	@Override
	protected String getCategoryXPath() {
		return null;
	}

	@Override
	protected String getJobTypeXPath() {
		return null;
	}

	@Override
	protected String getRefXPath() {
		return null;
	}

	@Override
	protected String getSpecXPath() {
		return "//div[@class='block job-description']";
	}

	@Override
	protected String getPreReqXPath() {
		return "//div[@class='block additional-requirements']";
	}

	@Override
	protected String getPostedDateXPath() {
		return null;
	}

	@Override
	protected String getApplyUrlXPath() {
		return null;
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}