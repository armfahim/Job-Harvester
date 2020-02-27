package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Eminence Capital job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Eminence-Jobs-E846286.htm
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-10
 */
@Service
public class EminenceCapital extends AbstractGlassDoor {
	
	private static final String SITE = ShortName.EMINENCE_CAPITAL;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
