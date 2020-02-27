package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Mimiro URL: https://complyadvantage.workable.com/
 * 
 * @author rafayet.hossain
 * @since 2019-03-31
 */
@Service
public class Mimiro extends AbstractWorkable {

	private static final String SITE = ShortName.MIMIROCOMPLYADVANTAGE;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
