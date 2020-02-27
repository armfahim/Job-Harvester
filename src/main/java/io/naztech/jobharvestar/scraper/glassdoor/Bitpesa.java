package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Bitpesa https://www.glassdoor.com/Jobs/BitPesa-Jobs-E1097843.htm
 * 
 * @author rafayet.hossain
 * @since 2019-03-31
 */
@Service
public class Bitpesa extends AbstractGlassDoor {

	private static final String SITE = ShortName.BITPESA;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
