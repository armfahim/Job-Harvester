package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Comerica job parsing class URL:
 * https://www.glassdoor.com/Jobs/Comerica-Jobs-E1281.htm
 * 
 * @author rafayet.hossain
 * @since 2019-03-06
 */
@Service
public class Comerica extends AbstractGlassDoor {

	private static final String SITE = ShortName.COMERICA;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
