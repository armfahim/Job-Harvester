package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * TravelBank Job parse  <br>
 * URL: https://www.glassdoor.com/Jobs/TravelBank-Jobs-E1410258.htm
 * 
 * @author jannatul.maowa
 * @since 2019-03-31
 */
@Service
public class TravelBank extends AbstractGlassDoor{

	private static final String SITE = ShortName.TRAVELBANK;

	@Override
	public String getSiteName() {
		return SITE;
	}
}