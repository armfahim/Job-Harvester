package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Hike <br>
 * URL: https://www.glassdoor.com/Jobs/Signifyd-Jobs-E776012.htm
 * 
 * @author muhammad tarek
 * @since 2019-03-28
 */
@Service
public class Sicnifyd extends AbstractGlassDoor {
	private static final String SITE = ShortName.SICNIFYD;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
