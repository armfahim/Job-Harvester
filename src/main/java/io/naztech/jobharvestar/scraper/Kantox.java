package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Kantox job site parser<br>
 * URL: https://kantox.workable.com
 * 
 * @author mazhar.alam
 * @author iftekar.alam
 * @since 2019-03-25
 **/
@Service
public class Kantox extends AbstractWorkable {
	private static final String SITE = ShortName.KANTOX;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}