package io.naztech.jobharvestar.scraper.glassdoor;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Lemonade
 * URL: https://lemonade.workable.com/
 * 
 * @author rafayet.hossain
 * @since 2019-03-31
 */
@Service
public class Lemonade extends AbstractGlassDoor {

	private static final String SITE = ShortName.LEMONADE;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
