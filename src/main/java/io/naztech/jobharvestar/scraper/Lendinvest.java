package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Lendinvest jobsite parse <br>
 * URL: https://apply.workable.com/lendinvest/
 * 
 * @author sohid.ullah
 * @author iftekar.alam
 * @since 2019-03-28
 * 
 **/
@Service
public class Lendinvest extends AbstractWorkable {
	private static final String SITE = ShortName.LENDINVEST;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}