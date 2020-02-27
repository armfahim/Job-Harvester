package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * SmartHR job site parsing class<br>
 * url: https://www.glassdoor.com/Jobs/SmartHR-Jobs-E1332614.htm
 * 
 * @author bm.alamin
 * Since: 2019-02-04
 */
@Service
public class SmartHr extends AbstractGlassDoor {
	private static final String SITE = ShortName.SMARTHR;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}