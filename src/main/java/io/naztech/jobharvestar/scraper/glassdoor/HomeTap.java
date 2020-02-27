package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * HomeTap  Job parse  <br>
 * https://www.glassdoor.com/Jobs/Hometap-Jobs-E1910156.htm
 * 
 * @author jannatul.maowa
 * @author iftekar.alam
 * @since 2019-03-31
 */
@Service
public class HomeTap extends AbstractGlassDoor{

	private static final String SITE = ShortName.HOMETAP;

	@Override
	public String getSiteName() {
		return SITE;
	}
	
	
}