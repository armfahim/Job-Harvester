package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Carlson Capital job site parsing class. <br>
 * URL: https://www.glassdoor.com/Jobs/Carlson-Capital-Jobs-E264473.htm
 * 
 * @author muhammad.tarek
 * @since 2019-03-07
 */
@Service
public class CarlsonCapital extends AbstractGlassDoor {
	private static final String SITE = ShortName.CARLSON_CAPITAL;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
