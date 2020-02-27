package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Graham Capital Management job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Graham-Capital-Management-Jobs-E149518.htm
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-10
 */
@Service
public class GrahamCapitalManagement extends AbstractGlassDoor {
	
	private static final String SITE = ShortName.GRAHAM_CAPITAL_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
