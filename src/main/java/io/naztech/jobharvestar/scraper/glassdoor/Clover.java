package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Clover job site parsing class<br>
 * url: https://www.glassdoor.com/Jobs/Clover-Jobs-E466202.htm
 * 
 * @author bm.alamin
 * 
 * Since: 2019-02-04
 */
@Service
public class Clover extends AbstractGlassDoor {
	private static final String SITE = ShortName.CLOVER;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}