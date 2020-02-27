package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Kyriba job site Scrapper class
 * url: https://www.glassdoor.com/Jobs/Kyriba-Corp-Jobs-E260305.htm
 * @author bm.alamin
 *
 * Since: 2019-04-01
 */
@Service
public class Kyriba extends AbstractGlassDoor{
	private static final String SITE = ShortName.KYRIBA;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
