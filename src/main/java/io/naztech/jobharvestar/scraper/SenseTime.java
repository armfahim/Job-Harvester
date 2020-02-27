package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * SenseTime job site parser. <br>
 * URL: https://www.glassdoor.com/Jobs/SenseTime-Jobs-E1165309.htm
 * 
 * @author Armaan Seraj Choudhury
 * @author tanmoy.tushar
 * @since 2019-03-14
 */
@Service
public class SenseTime extends AbstractGlassDoor {
	private static final String SITE = ShortName.SENSETIME;
	@Override
	public String getSiteName() {
		return SITE;
	}
	
}
