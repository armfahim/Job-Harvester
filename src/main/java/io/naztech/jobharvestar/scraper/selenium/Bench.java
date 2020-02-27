package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Bench job site scraper. <br>
 * URL:https://bench.co/careers/#current_openings
 * 
 * @author Asadullah Galib
 * @since 2019-03-27
 * 
 * @author tanmoy.tushar
 * @since 2014-04-25
 */
@Service
public class Bench extends AbstractSeleniumJobList {
	private static final String SITE = ShortName.BENCH;
	
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
		return "//table[@class='JobBoard__JobTable-sc-1gb1f1f-2 jnSwkw']/tbody/tr/td/a";
	}
	
	@Override
	protected String getFirstPageCatPath() {
		return "//table[@class='JobBoard__JobTable-sc-1gb1f1f-2 jnSwkw']/tbody/tr/td/div";
	}

	@Override
	protected String getFirstPageLocPath() {
		return null;
	}

	@Override
	protected String getTitleXPath() {
		return "//h1";
	}

	@Override
	protected String getLocationXPath() {
		return null;
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
		return "//div[@id='article-body']";
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