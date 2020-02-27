package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Bitgo URL: https://bitgo.workable.com/
 * 
 * @author rafayet.hossain
 * @since 2019-03-31
 */
@Service
public class Bitgo extends AbstractGlassDoor {

	private static final String SITE = ShortName.BITGO;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
