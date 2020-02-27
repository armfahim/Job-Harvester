package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * WintonGroup job site parser. <br>
 * URL: https://www.winton.com/opportunities
 * 
 * @author farzana.islam
 * @since 2019-03-05
 * 
 * @author tanmoy.tushar
 * @since 2019-04-10
 */
@Service
public class WintonGroup extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.WINTON_GROUP;
	
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
		return "//div[@class='jobList__col d-inline-block col-6 col-md-2 text-right']/a";
	}
	
	@Override
	protected String getFirstPageCatPath() {
		return null;
	}

	@Override
	protected String getFirstPageLocPath() {
		return "//div[@class='jobList__col d-inline-block col-6 col-md-3']/h4";
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
		return "p[class=jv-job-detail-meta]";
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
		return "div[class=jv-job-detail-description]";
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
		return "div[class=jv-job-detail-top-actions]>a";
	}
	
	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}
