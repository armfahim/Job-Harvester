package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * cloudmargin jobs site parser<br>
 * URL: https://cloudmargin.bamboohr.com/jobs/
 * Original Company URL: https://cloudmargin.bamboohr.com/jobs/
 * 
 * @author sohid.ullah
 * @since 2019-03-27
 */
@Service
public class CloudMargin extends AbstractBambooHr {
	private static final String SITE = ShortName.CLOUDMARGIN;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
