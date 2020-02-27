package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Centerbridge Partners job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Centerbridge-Partners-Jobs-E273063.htm
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-10
 */
@Service
public class CenterbridgePartners extends AbstractGlassDoor {
	
	private static final String SITE = ShortName.CENTERBRIDGE_PARTNERS;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
