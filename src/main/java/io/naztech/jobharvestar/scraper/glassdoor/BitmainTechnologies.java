package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Bitmain Technologies
 * URL : https://www.glassdoor.com/Jobs/Bitmain-Jobs-E2183741.htm
 * 
 * @author Armaan Seraj Choudhury
 * @author fahim.reza
 * @since 2019-03-11
 */
@Service
public class BitmainTechnologies extends AbstractGlassDoor {
	private static final String SITE = ShortName.BITMAIN_TECHNOLOGIES;


	@Override
	public String getSiteName() {
		return SITE;
	}
}
