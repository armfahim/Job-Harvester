package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * leveris jobs site parser<br>
 * URL: https://apply.workable.com/leveris/
 * 
 * @author kowshik.saha
 * @author iftekar.alam
 * @since 2019-04-09
 */
@Service
public class Leveris extends AbstractWorkable {
	private static final String SITE = ShortName.LEVERIS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
