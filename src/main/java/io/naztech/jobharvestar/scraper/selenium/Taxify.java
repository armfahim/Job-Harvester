package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Taxify job site parser. <br>
 * URL: https://careers.bolt.eu/positions/
 * 
 * @author sohid.ullah
 * @since 2019-03-19
 * 
 * @author tanmoy.tushar
 * @since 2019-04-28
 */
@Service
public class Taxify extends AbstractSeleniumJobList {
	private static final String SITE = ShortName.TAXIFY;

	@Override
	protected String getRowListPath() {
		return "//div[@class='text-biggest color-text-brand-primary']/a";
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
		return "//div[@class='d-flex flex-column my-60 my-md-100 text-left text-md-center justify-content-center']/h1";
	}

	@Override
	protected String getLocationXPath() {
		return "//div[@class='text-default fw-normal mt-10']";
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
		return "//div[@id='ember15']";
	}

	@Override
	protected String getPreReqXPath() {
		return null;
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

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

	
}