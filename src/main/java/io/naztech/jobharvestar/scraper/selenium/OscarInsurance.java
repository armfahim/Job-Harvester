package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Oscar Health Insurance Co. Job Site Parser<br>
 * URL: https://www.hioscar.com/careers/search?department=-1&location=-1
 * 
 * @author Rahat Ahmad
 * @since 2019-03-31
 * 
 * @author tanmoy.tushar
 * @since 2019-04-28
 */
@Service
public class OscarInsurance extends AbstractSeleniumJobList {
	private static final String SITE = ShortName.OSCAR_HEALTH_INSURANCE_CO;
	
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
		return "//div[@class='h-31FnUEmhnmubV269m7Ld1w']/div/a";
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
		return "//h1[@class='h-2UdeBX1j4x8Nom0P6wkvQC']";
	}

	@Override
	protected String getLocationXPath() {
		return "//div[@class='h-2vthvLnHLGoV2wYCwHTo8z']";
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
		return "//div[@class='h-38U3l2MJ4lbrCiyylEXsMp']";
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
}
