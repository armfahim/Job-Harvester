package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * True Layer Jobsite Parser</a><br>
 * URL: https://truelayer.workable.com
 * 
 * @author Fahim Reza
 * @author iftekar.alam
 * @since 2019-03-25
 */
@Service
public class TrueLayer extends AbstractWorkable {

	private static final String SITE = ShortName.TRUELAYER;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}